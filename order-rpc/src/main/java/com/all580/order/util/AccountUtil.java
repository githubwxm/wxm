package com.all580.order.util;

import com.alibaba.fastjson.JSONObject;
import com.all580.order.api.OrderConstant;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.ConsumeDay;
import com.all580.order.dto.RefundDay;
import com.all580.order.entity.*;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.consts.ProductRules;
import com.all580.product.api.model.EpSalesInfo;
import com.framework.common.lang.Arith;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeanUtils;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/11 16:51
 */
@Slf4j
public class AccountUtil {
    /**
     * 获取利润
     * @param salesInfoList 销售链
     * @param epId 企业
     * @param date 游玩日期
     * @return
     */
    public static AccountDataDto generateAccountData(List<EpSalesInfo> salesInfoList, int epId, Date date) {
        int salePrice = 0;
        int buyPrice = 0;
        int saleEpId = epId;
        for (EpSalesInfo info : salesInfoList) {
            if (info.getSale_ep_id() == epId && salePrice == 0) {
                salePrice = info.getPrice();
            }
            if (info.getEp_id() == epId && buyPrice == 0) {
                buyPrice = info.getPrice();
                saleEpId = info.getSale_ep_id();
            }
        }
        salePrice = salePrice == 0 ? buyPrice : salePrice;
        return new AccountDataDto(salePrice, buyPrice, salePrice - buyPrice, date, saleEpId, 0, null);
    }

    /**
     * 解析分销链
     * @param daySalesList 每天所有企业的分销链
     * @param bookingDate 预定时间
     * @return
     */
    public static Map<Integer, Collection<AccountDataDto>> parseEpSales(List<List<EpSalesInfo>> daySalesList, Date bookingDate) {
        Multimap<Integer, AccountDataDto> epAllDaysAccountData = ArrayListMultimap.create();
        int i = 0;
        for (List<EpSalesInfo> infoList : daySalesList) {
            Set<Integer> eps = new HashSet<>();
            Date day = DateUtils.addDays(bookingDate, i);
            for (EpSalesInfo info : infoList) {
                parseInAndOutPrice(epAllDaysAccountData, eps, infoList, info.getEp_id(), day);
                parseInAndOutPrice(epAllDaysAccountData, eps, infoList, info.getSale_ep_id(), day);
            }
            i++;
        }

        return epAllDaysAccountData.asMap();
    }

    /**
     * 设置分账数据的平台商ID
     * @param coreEpIds 需要获取平台商ID的企业ID集合
     * @param map 分账数据
     */
    public static void setAccountDataCoreEpId(Map<Integer, Integer> coreEpIds, Map<Integer, Collection<AccountDataDto>> map) {
        for (Integer epId : map.keySet()) {
            Integer coreEpId = coreEpIds.get(epId);
            for (AccountDataDto dataDto : map.get(epId)) {
                dataDto.setCoreEpId(coreEpId);
                if (coreEpIds.containsKey(dataDto.getSaleEpId())) {
                    dataDto.setSaleCoreEpId(coreEpIds.get(dataDto.getSaleEpId()));
                }
            }
        }
    }

    /**
     * 支付分账
     * @param salesInfoMap 销售链信息
     * @param orderItem 子订单
     * @param finalEpId 最终售卖企业
     * @return
     */
    public static List<OrderItemAccount> paySplitAccount(Map<Integer, Collection<AccountDataDto>> salesInfoMap, OrderItem orderItem, int finalEpId) {
        List<OrderItemAccount> accounts = new ArrayList<>();
        for (Integer epId : salesInfoMap.keySet()) {
            ArrayList<AccountDataDto> dataDtoList = Lists.newArrayList(salesInfoMap.get(epId));
            AccountDataDto firstDto = dataDtoList.get(0);
            // 获取总利润(所有天的)
            int totalProfit = getTotalProfit(dataDtoList) * orderItem.getQuantity();

            // 到付
            if (orderItem.getPayment_flag() == ProductConstants.PayType.PAYS) {
                calcPaysAccount(salesInfoMap, orderItem, finalEpId, accounts, epId, dataDtoList, firstDto, totalProfit);
                continue;
            }
            // 预付
            calcPrepayAccount(orderItem, accounts, epId, dataDtoList, firstDto, totalProfit);
        }
        return accounts;
    }

    /**
     * 退款分账
     * @param order 订单
     * @param accounts 分账记录
     * @param detailList 订单详情
     * @param refundDays 退订天数信息
     * @param from 销售侧/供应侧
     * @param refundOrderId 退订订单号
     * @param refundDate 退订时间
     * @return
     */
    public static List<RefundAccount> refundSplitAccount(Order order, Collection<OrderItemAccount> accounts, Collection<OrderItemDetail> detailList, Collection<RefundDay> refundDays, int from, int refundOrderId, Date refundDate) {
        List<RefundAccount> accountList = new ArrayList<>();
        Collection<AccountDataDto> finalAccountData = getFinalAccountData(accounts, order.getBuy_ep_id(), order.getPayee_ep_id());
        for (OrderItemAccount account : accounts) {
            Collection<AccountDataDto> dataList = decompileAccountData(account.getData());
            int money = 0;
            int cash = 0;
            for (RefundDay refundDay : refundDays) {
                OrderItemDetail detail = getDetailByDay(detailList, refundDay.getDay());
                if (detail == null) {
                    throw new ApiException(String.format("日期:%s没有订单数据,数据异常", JsonUtils.toJson(refundDay.getDay())));
                }
                AccountDataDto dataDto = getAccountDataByDay(dataList, refundDay.getDay());
                if (dataDto == null) {
                    throw new ApiException(String.format("日期:%s没有分账数据,数据异常", refundDay.getDay()));
                }

                Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
                // 计算退款手续费百分比
                double percent = getRefundFeePercent(finalAccountData, refundDay.getDay(), rate);
                // 退余额(当天利润*张数 * (1 - 手续费百分比)
                money += Arith.round(Arith.mul(dataDto.getProfit() * refundDay.getQuantity(), 1 - percent), 0);
                // 进可提现(当天利润*张数 *手续费百分比)
                cash += Arith.round(Arith.mul(dataDto.getProfit() * refundDay.getQuantity(), percent), 0);
            }

            RefundAccount refundAccount = new RefundAccount();
            refundAccount.setEp_id(account.getEp_id());
            refundAccount.setCore_ep_id(account.getCore_ep_id());
            refundAccount.setMoney(money == 0 ? 0 : account.getMoney() > 0 ? -money : money);
            refundAccount.setProfit(cash == 0 ? 0 : cash);
            refundAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
            refundAccount.setRefund_order_id(refundOrderId);
            refundAccount.setData(account.getData());
            accountList.add(refundAccount);
        }
        return accountList;
    }

    /**
     * 计算退支付金额
     * @param from 供应侧/销售侧
     * @param refundDays 每日退票详情
     * @param detailList 每日订单详情
     * @return 0:退支付金额 1:手续费
     */
    public static int[] calcRefundMoneyAndFee(OrderItemAccount finalAccount, int from, Collection<RefundDay> refundDays, Collection<OrderItemDetail> detailList, Date refundDate) {
        Collection<AccountDataDto> dataList = decompileAccountData(finalAccount.getData());
        if (dataList.size() != detailList.size()) {
            throw new ApiException("数据异常,分账记录的每日单价与订单详情不匹配");
        }
        int money = 0;
        int fee = 0;
        for (RefundDay refundDay : refundDays) {
            OrderItemDetail detail = getDetailByDay(detailList, refundDay.getDay());
            if (detail == null) {
                throw new ApiException(String.format("日期:%s没有订单数据", JsonUtils.toJson(refundDay.getDay())));
            }
            AccountDataDto dataDto = getAccountDataByDay(dataList, refundDay.getDay());
            if (dataDto == null) {
                throw new ApiException(String.format("日期:%s没有分账数据,数据异常", refundDay.getDay()));
            }

            Map<String, Integer> rate = ProductRules.calcRefund(from == ProductConstants.RefundEqType.SELLER ? detail.getCust_refund_rule() : detail.getSaler_refund_rule(), detail.getDay(), refundDate);
            // 计算该天手续费
            float dayFee = rate.get("type") == ProductConstants.AddPriceType.FIX ? rate.get("fixed") * refundDay.getQuantity() : dataDto.getOutPrice() * refundDay.getQuantity() * (float)rate.get("percent") / 100;
            fee += dayFee;
            money += dataDto.getOutPrice() * refundDay.getQuantity() - dayFee;
            refundDay.setFee((int) dayFee);
        }
        return new int[]{money, fee};
    }

    /**
     * 生成预付核销OR反核销余额变动信息
     * @param accounts 分账信息
     * @param day 那一天
     * @param quantity 张数
     * @param consume 是否核销
     * @return
     */
    public static List<BalanceChangeInfo> makerConsumeOrReConsumeBalanceChangeInfo(List<OrderItemAccount> accounts, Date day, int quantity, boolean consume) {
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        for (OrderItemAccount account : accounts) {
            if (account.getProfit() > 0) {
                Collection<AccountDataDto> dataDtoCollection = decompileAccountData(account.getData());
                AccountDataDto dataDto = getAccountDataByDay(dataDtoCollection, day);
                if (dataDto == null) {
                    throw new ApiException(JsonUtils.toJson(day) + " 没有分账数据");
                }
                int money = dataDto.getProfit() * quantity;
                if (money == 0) continue;
                BalanceChangeInfo changeInfo = new BalanceChangeInfo();
                changeInfo.setEp_id(account.getEp_id());
                changeInfo.setCore_ep_id(account.getCore_ep_id());
                changeInfo.setCan_cash(consume ? money : -money);
                changeInfo.setCan_cash_type(consume ? PaymentConstant.BalanceChangeType.ORDER_CANCEL : PaymentConstant.BalanceChangeType.ORDER_REVERSE);
                balanceChangeInfoList.add(changeInfo);
                account.setSettled_money(consume ? account.getSettled_money() + money : account.getSettled_money() - money); // 设置已结算金额
                account.setStatus(OrderConstant.AccountSplitStatus.HAS);
            }
        }
        return balanceChangeInfoList;
    }

    /**
     * 生成到付核销OR反核销余额变动信息
     * @param accounts 分账信息
     * @param day 那一天
     * @param quantity 张数
     * @param consume 是否核销
     * @return
     */
    public static List<BalanceChangeInfo> makerCashConsumeOrReConsumeBalanceChangeInfo(List<OrderItemAccount> accounts, Date day, int quantity, int finalEpId, int finalCoreEpId, boolean consume) {
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        for (OrderItemAccount account : accounts) {
            Collection<AccountDataDto> dataDtoCollection = decompileAccountData(account.getData());
            ArrayList<AccountDataDto> dataDtoList = Lists.newArrayList(dataDtoCollection);
            // 最终售价
            int finalPrice = getFinalPrice(accounts, finalEpId, finalCoreEpId) * quantity;
            // 天分账数据
            AccountDataDto dataDto = getAccountDataByDay(dataDtoCollection, day);
            if (dataDto == null) {
                throw new ApiException(JsonUtils.toJson(day) + " 没有分账数据");
            }
            int money = 0;
            // 供应商 扣钱
            if (account.getEp_id().intValue() == account.getSale_ep_id()) {
                int totalOutPrice = dataDto.getOutPrice() * quantity;
                if (finalPrice < totalOutPrice) {
                    throw new ApiException("最终售价小于供应商出货价");
                }
                money = -(finalPrice - totalOutPrice);
            } else {
                AccountDataDto firstDto = dataDtoList.get(0);
                // 跨平台
                if (firstDto.getCoreEpId() == account.getEp_id()) {
                    // 外部帐号 加钱
                    if (firstDto.getCoreEpId() != account.getCore_ep_id()) {
                        int totalInPrice = dataDto.getInPrice() * quantity;
                        money = finalPrice - totalInPrice;
                    } else {
                        // 内部帐号 扣钱
                        int totalOutPrice = dataDto.getOutPrice() * quantity;
                        money = -(finalPrice - totalOutPrice);
                    }
                } else {
                    // 其它平台内企业 利润
                    if (account.getProfit() > 0) {
                        money = dataDto.getProfit() * quantity;
                    }
                }
            }
            if (money == 0) continue;
            BalanceChangeInfo changeInfo = new BalanceChangeInfo();
            changeInfo.setEp_id(account.getEp_id());
            changeInfo.setCore_ep_id(account.getCore_ep_id());
            changeInfo.setCan_cash(consume ? money : -money);
            changeInfo.setBalance(changeInfo.getCan_cash());
            changeInfo.setCan_cash_type(consume ? PaymentConstant.BalanceChangeType.ORDER_CANCEL : PaymentConstant.BalanceChangeType.ORDER_REVERSE);
            changeInfo.setBalance_type(changeInfo.getCan_cash_type());
            balanceChangeInfoList.add(changeInfo);
            account.setSettled_money(consume ? account.getSettled_money() + money : account.getSettled_money() - money); // 设置已结算金额
            account.setStatus(OrderConstant.AccountSplitStatus.HAS);
        }
        return balanceChangeInfoList;
    }

    /**
     * 生成支付余额变动信息
     * @param accounts 分账信息
     * @param supplierEpId 底层供应商ID
     * @param buyEpId 购买企业ID
     * @return
     */
    public static List<BalanceChangeInfo> makerPayBalanceChangeInfo(List<OrderItemAccount> accounts, int payType, int supplierEpId, int supplierCoreEpId, int buyEpId) {
        List<BalanceChangeInfo> infoList = new ArrayList<>();
        for (OrderItemAccount account : accounts) {
            BalanceChangeInfo info = new BalanceChangeInfo();
            info.setEp_id(account.getEp_id());
            info.setCore_ep_id(account.getCore_ep_id());
            info.setBalance(account.getMoney());
            if (account.getMoney() < 0) {
                info.setCan_cash(account.getMoney());
            }
            infoList.add(info);
            // 设置变动类型
            // 到付
            if (payType == ProductConstants.PayType.PAYS) {
                // 到付产品只有核销会触发，所以余额、可提现金额一起变动 类型一致
                if (info.getBalance() < 0) {
                    // 到付支付
                    info.setBalance_type(PaymentConstant.BalanceChangeType.FREIGHT_PAY);
                    info.setCan_cash_type(PaymentConstant.BalanceChangeType.FREIGHT_PAY);
                } else {
                    // 到付分账
                    info.setBalance_type(PaymentConstant.BalanceChangeType.FREIGHT_ROUTING);
                    info.setCan_cash_type(PaymentConstant.BalanceChangeType.FREIGHT_ROUTING);
                }
                continue;
            }
            // 预付
            // 供应商 || 跨平台销售侧平台商
            if (supplierEpId == info.getEp_id() || (info.getEp_id().intValue() == info.getCore_ep_id() && info.getEp_id() != supplierCoreEpId)) {
                // 产品销售
                info.setBalance_type(PaymentConstant.BalanceChangeType.PRODUCT_SALES_PROFIT);
                continue;
            }
            // 平台商 && 外部帐号
            if (info.getEp_id().intValue() == info.getCore_ep_id() && info.getCore_ep_id() != info.getEp_id().intValue()){
                // 余额支付
                info.setBalance_type(PaymentConstant.BalanceChangeType.BALANCE_PAY);
                info.setCan_cash_type(PaymentConstant.BalanceChangeType.BALANCE_PAY);
                continue;
            }
            if (buyEpId == info.getEp_id()) {
                if (info.getBalance() < 0) {
                    // 余额支付
                    info.setBalance_type(PaymentConstant.BalanceChangeType.BALANCE_PAY);
                    info.setCan_cash_type(PaymentConstant.BalanceChangeType.BALANCE_PAY);
                    continue;
                }
                // 第三方支付分润
                info.setBalance_type(PaymentConstant.BalanceChangeType.THIRD_PAYMENT_PROFIT);
            }
            // 销售分润
            info.setBalance_type(PaymentConstant.BalanceChangeType.SALES_PROFIT);
        }
        return infoList;
    }

    /**
     * 生成退款余额变动信息
     * @param accounts 分账信息
     * @return
     */
    public static List<BalanceChangeInfo> makerRefundBalanceChangeInfo(Collection<RefundAccount> accounts) {
        List<BalanceChangeInfo> infoList = new ArrayList<>();
        for (RefundAccount account : accounts) {
            BalanceChangeInfo info = new BalanceChangeInfo();
            info.setEp_id(account.getEp_id());
            info.setCore_ep_id(account.getCore_ep_id());
            info.setBalance(account.getMoney());
            info.setCan_cash(account.getProfit());
            account.setStatus(OrderConstant.AccountSplitStatus.HAS);
            infoList.add(info);
            // 设置变动类型
            if (info.getBalance() > 0) {
                // 退订返回
                info.setBalance_type(PaymentConstant.BalanceChangeType.CANCEL_THE_REFUND);
                info.setCan_cash_type(PaymentConstant.BalanceChangeType.CANCEL_THE_REFUND);
                continue;
            }
            // 退订退款
            info.setBalance_type(PaymentConstant.BalanceChangeType.ORDER_REFUND);
            if (info.getCan_cash() >= 0) {
                // 退订手续费
                info.setCan_cash_type(PaymentConstant.BalanceChangeType.REFUND_FEE);
            }
        }
        return infoList;
    }

    /**
     * JSON转java分账数据类型
     * @return
     */
    public static Map<Integer, Collection<AccountDataDto>> decompileEpAccountData(String json) {
        Multimap<Integer, AccountDataDto> multimap = ArrayListMultimap.create();
        try {
            Map map = JsonUtils.json2Map(json);
            for (Object key : map.keySet()) {
                List list = (List) map.get(key);
                for (Object value : list) {
                    multimap.put(CommonUtil.objectParseInteger(key), JsonUtils.map2obj((Map) value, AccountDataDto.class));
                }
            }
        } catch (Exception e) {
            log.error("JSON:" + json + " 反编译为JAVA类型失败", e);
            throw new ApiException("分账销售链反编译JAVA类型失败", e);
        }
        return multimap.asMap();
    }

    /**
     * JSON转java分账数据类型
     * @param json
     * @return
     */
    public static Collection<AccountDataDto> decompileAccountData(String json) {
        Collection<AccountDataDto> collection = new ArrayList<>();
        try {
            List list = JsonUtils.json2List(json);
            for (Object value : list) {
                collection.add(JsonUtils.map2obj((Map) value, AccountDataDto.class));
            }
        } catch (Exception e) {
            log.error("JSON:" + json + " 反编译为JAVA类型失败", e);
            throw new ApiException("分账数据反编译JAVA类型失败", e);
        }
        return collection;
    }

    /**
     * JSON转java退票数据类型
     * @param list
     * @return
     */
    public static Collection<RefundDay> decompileRefundDay(List list) {
        Collection<RefundDay> collection = new ArrayList<>();
        try {
            for (Object day : list) {
                RefundDay refundDay = JsonUtils.map2obj((Map) day, RefundDay.class);
                if (refundDay.getQuantity() <= 0) continue;
                collection.add(refundDay);
            }
        } catch (Exception e) {
            log.error("反编译为JAVA类型失败", e);
            throw new ApiException("退票天数据反编译JAVA类型失败", e);
        }
        return collection;
    }

    /**
     * JSON转java退票数据类型
     * @param json
     * @return
     */
    public static Collection<RefundDay> decompileRefundDay(String json) {
        return decompileRefundDay(JsonUtils.json2List(json));
    }

    public static Collection<RefundDay> parseRefundDayForDetail(Collection<OrderItemDetail> details) {
        Collection<RefundDay> collection = new ArrayList<>();
        for (OrderItemDetail detail : details) {
            RefundDay refundDay = new RefundDay();
            refundDay.setDay(detail.getDay());
            int quantity;
            if (detail.getUsed_quantity() != null && detail.getUsed_quantity() > 0) {
                if (detail.getLow_quantity() != null && detail.getLow_quantity() > 0 && detail.getUsed_quantity() < detail.getLow_quantity()) {
                    quantity = detail.getQuantity() - detail.getLow_quantity();
                } else {
                    quantity = detail.getQuantity() - detail.getUsed_quantity();
                }
            } else {
                quantity = detail.getQuantity();
            }
            if (quantity <= 0) continue;
            refundDay.setQuantity(quantity);
            refundDay.setVisitors(Collections.<Visitor>emptyList());
            collection.add(refundDay);
        }
        return collection;
    }

    /**
     * JSON转java退票数据类型
     * @param list
     * @return
     */
    public static Collection<ConsumeDay> decompileConsumeDay(List list) {
        Collection<ConsumeDay> collection = new ArrayList<>();
        try {
            for (Object day : list) {
                ConsumeDay consumeDay = JsonUtils.map2obj((Map) day, ConsumeDay.class);
                if (consumeDay.getQuantity() <= 0) continue;
                collection.add(consumeDay);
            }
        } catch (Exception e) {
            log.error("反编译为JAVA类型失败", e);
            throw new ApiException("核销天数据反编译JAVA类型失败", e);
        }
        return collection;
    }

    /**
     * 获取某天详情
     * @param detailList 天数据
     * @param day 某一天
     * @return
     */
    public static OrderItemDetail getDetailByDay(Collection<OrderItemDetail> detailList, Date day) {
        if (detailList == null || day == null) {
            return null;
        }
        for (OrderItemDetail detail : detailList) {
            if (detail.getDay().equals(day)) {
                return detail;
            }
        }
        return null;
    }

    public static int getRefundQuantity(Collection<RefundDay> refundDays) {
        int totalRefundQuantity = 0;
        for (RefundDay refundDay : refundDays) {
            totalRefundQuantity += refundDay.getQuantity();
        }
        return totalRefundQuantity;
    }

    /**
     * 根据每天游客张数获取所有游客张数
     * @param totalVisitors 总张数游客集合
     * @param visitors
     * @return
     */
    public static Collection<Visitor> parseTotalVisitorQuantityForEveryDay(Collection<Visitor> totalVisitors, Collection<Visitor> visitors) {
        for (Visitor visitor : visitors) {
            boolean have = false;
            for (Visitor v : totalVisitors) {
                if (visitor.getId().equals(v.getId())) {
                    v.setQuantity(v.getQuantity() + visitor.getQuantity());
                    have = true;
                    break;
                }
            }
            if (!have) {
                totalVisitors.add(visitor);
            }
        }
        return totalVisitors;
    }

    public static boolean canRefund(String rule) {
        try {
            return JSONObject.parseObject(rule).getBooleanValue("refund");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 转换套票分账数据
     * @param accounts
     * @return
     */
    public static List<OrderItemAccount> packageAccount2Account(Collection<PackageOrderItemAccount> accounts) {
        List<OrderItemAccount> itemAccounts = new ArrayList<>();
        for (PackageOrderItemAccount account : accounts) {
            OrderItemAccount itemAccount = new OrderItemAccount();
            BeanUtils.copyProperties(account, itemAccount);
            itemAccounts.add(itemAccount);
        }
        return itemAccounts;
    }

    /**
     * 获取最终售价
     * @param salesInfo 最终售卖企业信息
     * @return
     */
    private static int getFinalPrice(Collection<AccountDataDto> salesInfo) {
        int finalPrice = 0;
        for (AccountDataDto dataDto : salesInfo) {
            finalPrice += dataDto.getOutPrice() == 0 ? dataDto.getInPrice() : dataDto.getOutPrice();
        }
        return finalPrice;
    }

    /**
     * 获取最终售价
     * @param accounts 分账数据
     * @param finalEpId 最终售卖企业ID
     * @return
     */
    private static int getFinalPrice(Collection<OrderItemAccount> accounts, int finalEpId, int finalCoreEpId) {
        for (OrderItemAccount account : accounts) {
            if (account.getEp_id() == finalEpId && finalCoreEpId == account.getCore_ep_id()) {
                Collection<AccountDataDto> dataDtoCollection = decompileAccountData(account.getData());
                return getFinalPrice(dataDtoCollection);
            }
        }
        return 0;
    }

    /**
     * 获取最终销售企业分账数据
     * @param accounts 分账数据
     * @param finalEpId 最终销售商ID
     * @param finalCoreEpId
     * @return
     */
    private static Collection<AccountDataDto> getFinalAccountData(Collection<OrderItemAccount> accounts, int finalEpId, int finalCoreEpId) {
        for (OrderItemAccount account : accounts) {
            if (account.getEp_id() == finalEpId && finalCoreEpId == account.getCore_ep_id()) {
                return decompileAccountData(account.getData());
            }
        }
        return null;
    }

    /**
     * 计算到付分账
     * @param salesInfoMap 企业所有天分账信息
     * @param orderItem 子订单
     * @param finalEpId 最终售卖企业
     * @param accounts 生成的分账数据存储
     * @param epId 当前计算企业ID
     * @param dataDtoList 当前企业AccountData
     * @param firstDto 第一天AccountData
     * @param totalProfit 总利润
     * @return
     */
    private static void calcPaysAccount(Map<Integer, Collection<AccountDataDto>> salesInfoMap, OrderItem orderItem, int finalEpId, List<OrderItemAccount> accounts, Integer epId, ArrayList<AccountDataDto> dataDtoList, AccountDataDto firstDto, int totalProfit) {
        Collection<AccountDataDto> finalAccountData = salesInfoMap.get(finalEpId);
        if (finalAccountData == null) {
            throw new ApiException("最终售卖企业错误");
        }
        // 获取最终售卖价格
        int finalPrice = getFinalPrice(finalAccountData) * orderItem.getQuantity();
        // 出货总价
        int totalOutPrice = getTotalOutPrice(dataDtoList) * orderItem.getQuantity();
        // 供应商到付要扣钱 = 最终售卖价格 - 出货价 出钱
        if (orderItem.getSupplier_ep_id() == epId.intValue()) {
            accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getCoreEpId(), -(finalPrice - totalOutPrice), totalProfit, dataDtoList, orderItem.getId()));
            return;
        }

        // 跨平台分账
        if (firstDto.getCoreEpId() == epId && firstDto.getCoreEpId() != firstDto.getSaleCoreEpId()) {
            // 到付不是平台商的产品 并且跨平台则不支持
            if (orderItem.getSupplier_core_ep_id() != orderItem.getSupplier_ep_id().intValue()) {
                throw new ApiException("不支持跨平台的到付不是平台商的产品");
            }
            // 进货总价
            int totalInPrice = getTotalInPrice(dataDtoList) * orderItem.getQuantity();
            // 平台外部账户 = 最终售卖价格 - 进货价 进钱
            accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getSaleCoreEpId(), finalPrice - totalInPrice, totalProfit, dataDtoList, orderItem.getId()));

            // 平台内部账户 = 最终售卖价格 - 出货价 出钱
            accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getCoreEpId(), -(finalPrice - totalOutPrice), totalProfit, dataDtoList, orderItem.getId()));
            return;
        }

        // 其它企业内部分账 利润
        accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getCoreEpId(), totalProfit, totalProfit, dataDtoList, orderItem.getId()));
    }

    /**
     * 计算预付分账
     * @param orderItem 子订单
     * @param accounts 生成的分账数据存储
     * @param epId 当前计算企业ID
     * @param dataDtoList 当前企业AccountData
     * @param firstDto 第一天AccountData
     * @param totalProfit 总利润
     */
    private static void calcPrepayAccount(OrderItem orderItem, List<OrderItemAccount> accounts, Integer epId, ArrayList<AccountDataDto> dataDtoList, AccountDataDto firstDto, int totalProfit) {
        // 跨平台分账
        if (firstDto.getCoreEpId() == epId && firstDto.getCoreEpId() != firstDto.getSaleCoreEpId()) {
            int totalOutPrice = getTotalOutPrice(dataDtoList) * orderItem.getQuantity();
            // 平台内部账户 进钱
            accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getCoreEpId(), totalOutPrice, totalProfit, dataDtoList, orderItem.getId()));

            // 平台外部账户 出钱
            int totalInPrice = getTotalInPrice(dataDtoList) * orderItem.getQuantity();
            accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getSaleCoreEpId(), -totalInPrice, totalProfit, dataDtoList, orderItem.getId()));
            return;
        }
        // 平台内部分账 利润
        accounts.add(makerAccount(firstDto.getSaleEpId(), epId, firstDto.getCoreEpId(), totalProfit, totalProfit, dataDtoList, orderItem.getId()));
    }

    private static int getTotalProfit(Collection<AccountDataDto> dataDtos) {
        int totalAddProfit = 0;
        // 卖家平台商每天的利润
        for (AccountDataDto dataDto : dataDtos) {
            totalAddProfit += dataDto.getProfit();
        }
        return totalAddProfit;
    }

    public static int getTotalOutPrice(Collection<AccountDataDto> dataDtos) {
        int totalOutPrice = 0;
        for (AccountDataDto dataDto : dataDtos) {
            totalOutPrice += dataDto.getOutPrice();
        }
        return totalOutPrice;
    }

    private static int getTotalInPrice(Collection<AccountDataDto> dataDtos) {
        int totalInPrice = 0;
        for (AccountDataDto dataDto : dataDtos) {
            totalInPrice += dataDto.getInPrice();
        }
        return totalInPrice;
    }

    private static OrderItemAccount makerAccount(int saleEpId, int epId, int coreEpId, int money, int totalProfit, List<AccountDataDto> dataDtoList, int itemId) {
        OrderItemAccount account = new OrderItemAccount();
        account.setSale_ep_id(saleEpId);
        account.setEp_id(epId);
        account.setCore_ep_id(coreEpId);
        account.setMoney(money);
        account.setProfit(totalProfit);
        account.setData(JsonUtils.toJson(dataDtoList));
        account.setOrder_item_id(itemId);
        account.setSettled_money(0);
        account.setStatus(OrderConstant.AccountSplitStatus.NOT);
        return account;
    }

    /**
     * 解析进货价和出货价 并封装分账数据
     * @param epAllDaysAccountData 企业所有天的分账数据
     * @param eps 企业集合(多个企业只执行一次)
     * @param infoList 一天的分销链
     * @param epId 企业
     * @param date 哪一天
     */
    private static void parseInAndOutPrice(Multimap<Integer, AccountDataDto> epAllDaysAccountData, Set<Integer> eps, List<EpSalesInfo> infoList, int epId, Date date) {
        if (!eps.contains(epId)) {
            epAllDaysAccountData.put(epId, generateAccountData(infoList, epId, date));
            eps.add(epId);
        }
    }

    /**
     * 获取某一天的分账数据
     * @param dataDtoCollection 所有天的分账数据
     * @param day 天
     * @return
     */
    private static AccountDataDto getAccountDataByDay(Collection<AccountDataDto> dataDtoCollection, Date day) {
        for (AccountDataDto dataDto : dataDtoCollection) {
            if (dataDto.getDay().getTime() == day.getTime()) {
                return dataDto;
            }
        }
        return null;
    }

    /**
     * 获取退款手续费百分比(固定扣费：单张扣费 / 最终售卖价格 保留4位小数；百分比扣费：单张扣费 / 100 保留4位小数;)
     * @param finalAccountData 最终销售分账数据
     * @param day 天
     * @param rate 退货规则
     * @return
     */
    private static double getRefundFeePercent(Collection<AccountDataDto> finalAccountData, Date day, Map<String, Integer> rate) {
        double percent = 1.0;
        if (rate.get("type") == ProductConstants.AddPriceType.FIX) {
            AccountDataDto dataDto = getAccountDataByDay(finalAccountData, day);
            if (dataDto == null) {
                throw new ApiException("日期:"+day+"最终销售商分账数据异常");
            }
            percent = Arith.div(rate.get("fixed"), dataDto.getOutPrice(), 4);
        } else {
            percent = Arith.div(rate.get("percent"), 100, 4);
        }
        return percent;
    }
}
