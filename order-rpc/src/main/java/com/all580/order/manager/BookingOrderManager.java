package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.GenerateAccountDto;
import com.all580.order.entity.*;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 预定
 * @date 2016/10/8 14:06
 */
@Component
@Slf4j
public class BookingOrderManager extends BaseOrderManager {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private OrderClearanceDetailMapper orderClearanceDetailMapper;
    @Autowired
    private ClearanceWashedSerialMapper clearanceWashedSerialMapper;

    /**
     * 验证游客信息
     * @param visitors 游客信息
     * @param productSubCode 子产品CODE
     * @param bookingDate 预定时间
     * @param maxCount 最大次数
     * @param maxQuantity 最大张数
     * @return
     */
    public Result validateVisitor(List<Map> visitors, Long productSubCode, Date bookingDate, Integer maxCount, Integer maxQuantity) {
        Set<String> sids = new HashSet<>();
        for (Map visitorMap : visitors) {
            String sid = CommonUtil.objectParseString(visitorMap.get("sid"));
            if (sids.contains(sid)) {
                return new Result<>(false, Result.PARAMS_ERROR, "身份证:" + sid + "重复");
            }
            if (!canOrderByCount(productSubCode, sid, bookingDate, maxCount)) {
                return new Result<>(false, Result.PARAMS_ERROR, "身份证:" + sid + "超出该产品当天最大订单数");
            }
            Integer qty = CommonUtil.objectParseInteger(visitorMap.get("quantity"));
            if (!canOrderByQuantity(productSubCode, sid, bookingDate, maxQuantity, qty)) {
                return new Result<>(false, Result.PARAMS_ERROR,
                        "身份证:" + sid + "超出该产品当天最大购票数,现已定" + qty + "张,最大购票" + maxQuantity + "张");
            }
            sids.add(sid);
        }
        return new Result(true);
    }

    /**
     * 判断身份证订单次数是否超过最大次数
     * @param productSubId 子产品ID
     * @param sid 身份证
     * @param date 预定日期
     * @param max 最大次数
     * @return
     */
    private boolean canOrderByCount(Long productSubCode, String sid, Date date, Integer max) {
        if (max == null || max <= 0)
            return true;
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        int count = orderItemMapper.countBySidAndProductForDate(productSubCode, sid, start, end);
        return count < max;
    }

    /**
     * 判断身份证订票张数是否超过最大张数
     * @param productSubCode 子产品CODE
     * @param sid 身份证
     * @param date 预定日期
     * @param max 最大张数
     * @param cur 当前需要订购张数
     * @return
     */
    private boolean canOrderByQuantity(Long productSubCode, String sid, Date date, Integer max, Integer cur) {
        if (max == null || max <= 0)
            return true;
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        int quantity = visitorMapper.quantityBySidAndProductForDate(productSubCode, sid, start, end);
        return quantity + cur <= max;
    }

    /**
     * 创建订单生成订单数据
     * @param coreEpId 操作平台商
     * @param buyEpId 销售企业ID
     * @param userId 销售用户ID
     * @param userName 销售用户名称
     * @param from 来源
     * @return
     */
    @Transactional
    public Order generateOrder(Integer coreEpId, Integer buyEpId, Integer userId, String userName, Integer from, String remark) {
        Order order = new Order();
        order.setNumber(UUIDGenerator.generateUUID());
        order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
        order.setBuyEpId(buyEpId);
        order.setBuyOperatorId(userId);
        order.setBuyOperatorName(userName);
        order.setCreateTime(new Date());
        order.setFromType(from);
        order.setRemark(remark);
        order.setPayeeEpId(coreEpId);
        orderMapper.insertSelective(order);
        return order;
    }

    /**
     * 创建子订单生成子订单数据
     * @param info 产品信息
     * @param saleAmount 进货价
     * @param days 天数
     * @param orderId 订单ID
     * @param quantity 张数
     * @return
     */
    @Transactional
    public OrderItem generateItem(ProductSalesInfo info, int saleAmount, Date bookingDate, int days, int orderId, int quantity, Integer proSubId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setNumber(UUIDGenerator.generateUUID());
        orderItem.setStart(bookingDate);
        Date end = DateUtils.addDays(info.getEndTime(), days - 1);
        orderItem.setEnd(end);
        orderItem.setSaleAmount(saleAmount); // 进货价
        orderItem.setDays(days);
        orderItem.setGroupId(0); // 散客为0
        orderItem.setOrderId(orderId);
        orderItem.setProName(info.getProductName());
        orderItem.setProSubName(info.getProductSubName());
        orderItem.setProSubNumber(info.getProductSubCode());
        orderItem.setProSubId(proSubId);
        orderItem.setQuantity(quantity);
        orderItem.setPaymentFlag(info.getPayType());
        orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
        orderItem.setSupplierEpId(info.getEpId());
        orderItem.setEpMaId(info.getEpMaId());
        orderItemMapper.insertSelective(orderItem);
        return orderItem;
    }

    /**
     * 生成分账（平账）
     * @param dto
     * @return
     */
    @Transactional
    public List<OrderItemAccount> generateAccount(GenerateAccountDto dto) {
        List<OrderItemAccount> accounts = new ArrayList<>();
        OrderItemAccount subtractAccount = new OrderItemAccount();
        subtractAccount.setEpId(dto.getSubtractEpId());
        subtractAccount.setCoreEpId(dto.getSubtractCoreId());
        subtractAccount.setOrderItemId(dto.getOrderItemId());
        subtractAccount.setMoney(-dto.getMoney());
        subtractAccount.setProfit(dto.getSubtractProfit());
        subtractAccount.setSettledMoney(0);
        subtractAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
        subtractAccount.setData(dto.getSubtractData() == null ? null : JsonUtils.toJson(dto.getSubtractData()));
        accounts.add(subtractAccount);
        orderItemAccountMapper.insertSelective(subtractAccount);

        OrderItemAccount addAccount = new OrderItemAccount();
        addAccount.setEpId(dto.getAddEpId());
        addAccount.setCoreEpId(dto.getAddCoreId());
        addAccount.setOrderItemId(dto.getOrderItemId());
        addAccount.setMoney(dto.getMoney());
        addAccount.setProfit(dto.getAddProfit());
        addAccount.setSettledMoney(0);
        addAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
        addAccount.setData(dto.getAddData() == null ? null : JsonUtils.toJson(dto.getAddData()));
        accounts.add(addAccount);
        orderItemAccountMapper.insertSelective(addAccount);
        return accounts;
    }

    /**
     * 创建子订单详情
     * @param info 产品信息
     * @param itemId 子订单ID
     * @param day 当天
     * @param quantity 张数
     * @return
     */
    @Transactional
    public OrderItemDetail generateDetail(ProductSalesInfo info, int itemId, Date day, int quantity) {
        OrderItemDetail orderItemDetail = new OrderItemDetail();
        orderItemDetail.setDay(day);
        orderItemDetail.setQuantity(quantity);
        orderItemDetail.setCustRefundRule(info.getCustRefundRule()); // 销售方退货规则
        orderItemDetail.setSalerRefundRule(info.getSalerRefundRule()); // 供应方退货规则
        orderItemDetail.setOrderItemId(itemId);
        orderItemDetail.setCreateTime(new Date());
        orderItemDetail.setDisableDay(info.getDisableDate());
        orderItemDetail.setDisableWeek(info.getDisableWeek());
        orderItemDetail.setUseHoursLimit(info.getUseHoursLimit());
        Date effectiveDate = null;
        if (orderItemDetail.getDay().before(orderItemDetail.getCreateTime())) {
            effectiveDate = DateUtils.addHours(orderItemDetail.getCreateTime(), orderItemDetail.getUseHoursLimit());
        } else {
            effectiveDate = DateUtils.addHours(orderItemDetail.getDay(), orderItemDetail.getUseHoursLimit());
        }
        orderItemDetail.setEffectiveDate(effectiveDate);
        Date expiryDate = null;
        if (info.getEffectiveType() == ProductConstants.EffectiveValidType.DAY) {
            // 这里目前只做了门票的,默认结束日期就是当天的,酒店应该是第二天
            expiryDate = DateUtils.addDays(orderItemDetail.getDay(), info.getEffectiveDay() - 1);
            expiryDate = DateUtils.setHours(expiryDate, DateFormatUtils.get(info.getEndTime(), Calendar.HOUR_OF_DAY));
            expiryDate = DateUtils.setMinutes(expiryDate, DateFormatUtils.get(info.getEndTime(), Calendar.MINUTE));
            expiryDate = DateUtils.setSeconds(expiryDate, DateFormatUtils.get(info.getEndTime(), Calendar.SECOND));
        } else {
            expiryDate = info.getEffectiveEndDate();
        }
        if (effectiveDate.after(expiryDate)) {
            throw new ApiException("该产品已过期");
        }
        orderItemDetail.setExpiryDate(expiryDate);
        orderItemDetail.setRefundQuantity(0);
        orderItemDetail.setUsedQuantity(0);
        orderItemDetailMapper.insertSelective(orderItemDetail);
        return orderItemDetail;
    }

    /**
     * 创建子订单游客信息
     * @param v 游客参数
     * @param itemDetailId 子订单详情ID
     * @return
     */
    @Transactional
    public Visitor generateVisitor(Map v, int itemDetailId) {
        Visitor visitor = new Visitor();
        visitor.setRefId(itemDetailId);
        visitor.setName(CommonUtil.objectParseString(v.get("name")));
        visitor.setPhone(CommonUtil.objectParseString(v.get("phone")));
        visitor.setSid(CommonUtil.objectParseString(v.get("sid")));
        visitor.setQuantity(CommonUtil.objectParseInteger(v.get("quantity")));
        visitorMapper.insertSelective(visitor);
        return visitor;
    }

    /**
     * 创建订单联系人
     * @param shippingMap 联系人参数
     * @param orderId 订单ID
     * @return
     */
    @Transactional
    public Shipping generateShipping(Map shippingMap, int orderId) {
        Shipping shipping = new Shipping();
        shipping.setOrderId(orderId);
        shipping.setName(CommonUtil.objectParseString(shippingMap.get("name")));
        shipping.setPhone(CommonUtil.objectParseString(shippingMap.get("phone")));
        shipping.setSid(CommonUtil.objectParseString(shippingMap.get("sid")));
        shippingMapper.insertSelective(shipping);
        return shipping;
    }

    /**
     * 判断所有子订单是否审核通过
     * @param orderId 订单ID
     * @param excludes 例外不做处理的
     * @return
     */
    public boolean isOrderAllAudit(int orderId, int... excludes) {
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            if (excludes != null && Arrays.binarySearch(excludes, orderItem.getId()) >= 0) {
                continue;
            }
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                return false;
            }
        }
        return true;
    }

    /**
     * 支付分账
     * @param orderItems 子订单
     * @return
     */
    @Transactional
    public void paySplitAccount(int orderId, List<OrderItem> orderItems) {
        if (orderItems == null) {
            orderItems = orderItemMapper.selectByOrderId(orderId);
        }
        // 遍历所有子订单封装分账数据统一分账
        List<BalanceChangeInfo> infoList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            List<OrderItemAccount> orderItemAccounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
            for (OrderItemAccount itemAccount : orderItemAccounts) {
                BalanceChangeInfo info = new BalanceChangeInfo();
                info.setEpId(itemAccount.getEpId());
                info.setCoreEpId(itemAccount.getCoreEpId());
                info.setBalance(itemAccount.getMoney());
                infoList.add(info);
                itemAccount.setStatus(OrderConstant.AccountSplitStatus.HAS);
                orderItemAccountMapper.updateByPrimaryKeySelective(itemAccount);
            }
        }
        // 调用分账
        Result<BalanceChangeRsp> result = changeBalances(PaymentConstant.BalanceChangeType.PAY_SPLIT, String.valueOf(orderId), infoList);
        if (!result.isSuccess()) {
            log.warn("支付分账失败:{}", result.get());
            throw new ApiException(result.getError());
        }
    }

    /**
     * 预分账
     * @param daySalesList 每天的销售链
     * @param itemId 子订单ID
     * @param quantity 每天(时间段)票数
     * @return
     */
    @Transactional
    public List<OrderItemAccount> preSplitAccount(List<List<EpSalesInfo>> daySalesList, int itemId, int quantity, int payType, Date bookingDate) {
        // 预分账记录
        List<OrderItemAccount> accounts = new ArrayList<>();
        // 缓存平台商ID
        Map<Integer, Integer> coreEpMap = new HashMap<>();
        // 每个企业所有天的利润
        Map<Integer, List<AccountDataDto>> daysAccountDataMap = new HashMap<>();
        // 叶子销售商最终卖价
        int salePrice = 0;
        // 遍历每天的销售链
        int i = 0;
        for (List<EpSalesInfo> infoList : daySalesList) {
            // 一天的利润单价
            Map<Integer, AccountDataDto> dayAccountDataMap = new HashMap<>();
            Date day = DateUtils.addDays(bookingDate, i);
            for (EpSalesInfo info : infoList) {
                Integer buyCoreEpId = null;
                // 叶子销售商门市价销售
                if (info.getBuyEpId() == -1) {
                    buyCoreEpId = getCoreEp(coreEpMap, info.getSaleEpId());
                    salePrice = info.getPrice();
                } else {
                    buyCoreEpId = getCoreEp(coreEpMap, info.getBuyEpId());
                }
                Integer saleCoreEpId = getCoreEp(coreEpMap, info.getSaleEpId());
                // 卖家 == 平台商 && 买家 == 平台商
                if (info.getBuyEpId() == buyCoreEpId.intValue() && saleCoreEpId.intValue() == info.getSaleEpId()) {
                    AccountDataDto dto = addDayAccount(dayAccountDataMap, infoList, buyCoreEpId, day);
                    dto.setSaleCoreEpId(saleCoreEpId);
                    addDayAccount(dayAccountDataMap, infoList, saleCoreEpId, day);
                }

                // 买家平台商ID == 卖家平台商ID && 卖家ID != 卖家平台商ID
                if (buyCoreEpId.intValue() == saleCoreEpId/* && info.getSaleEpId() != saleCoreEpId*/) {
                    AccountDataDto dto = addDayAccount(dayAccountDataMap, infoList, info.getSaleEpId(), day);
                    dto.setSaleCoreEpId(saleCoreEpId);
                }
            }

            // 把一天的利润添加到所有天当中
            for (Integer epId : dayAccountDataMap.keySet()) {
                List<AccountDataDto> accountDataDtoList = daysAccountDataMap.get(epId);
                if (accountDataDtoList == null) {
                    accountDataDtoList = new ArrayList<>();
                    daysAccountDataMap.put(epId, accountDataDtoList);
                }
                accountDataDtoList.add(dayAccountDataMap.get(epId));
            }
            i++;
        }

        // 把每天的利润集合做分账
        // 平台商本企业内部分账一次扣款
        Map<Integer, Integer> coreSubMap = new HashMap<>();
        for (Integer epId : daysAccountDataMap.keySet()) {
            Integer coreEpId = getCoreEp(coreEpMap, epId);
            List<AccountDataDto> dataDtos = daysAccountDataMap.get(epId);
            int totalInPrice = 0;
            int totalProfit = 0;
            for (AccountDataDto dataDto : dataDtos) {
                totalInPrice += dataDto.getInPrice();
                totalProfit += dataDto.getProfit();
            }
            // 平台商之间分账(进货价)
            if (coreEpId.intValue() == epId) {
                AccountDataDto dto = dataDtos.get(0);
                if (dto != null && dto.getSaleCoreEpId() != null) {
                    if (dto.getSaleCoreEpId().intValue() == epId) {
                        continue;
                    }
                    GenerateAccountDto accountDto = new GenerateAccountDto();

                    // 卖家平台商每天的利润
                    List<AccountDataDto> saleAccountDataDtoList = daysAccountDataMap.get(dto.getSaleCoreEpId());
                    int totalAddProfit = getTotalProfit(saleAccountDataDtoList);
                    // 预付
                    if (payType == ProductConstants.PayType.PREPAY) {
                        accountDto.setSubtractEpId(epId); // 买家 扣钱
                        accountDto.setSubtractCoreId(dto.getSaleCoreEpId()); // 在卖家的平台商
                        accountDto.setAddEpId(dto.getSaleCoreEpId()); // 卖家的平台商收钱
                        accountDto.setAddCoreId(dto.getSaleCoreEpId());
                        accountDto.setMoney(totalInPrice * quantity); // 金额 进货价 * 票数
                        accountDto.setSubtractProfit(totalProfit * quantity); // 买家每天的总利润 * 票数
                        accountDto.setAddProfit(totalAddProfit * quantity); // 卖家每天的总利润 * 票数
                        accountDto.setOrderItemId(itemId);
                        accountDto.setSubtractData(dataDtos); // 买家每天的单价利润
                        accountDto.setAddData(saleAccountDataDtoList); // 卖家每天的单价利润
                    } else {
                        // 到付
                        accountDto.setSubtractEpId(dto.getSaleCoreEpId()); // 卖家 扣钱
                        accountDto.setSubtractCoreId(dto.getSaleCoreEpId()); // 在卖家的平台商
                        accountDto.setAddEpId(epId); // 买家的平台商收钱
                        accountDto.setAddCoreId(dto.getSaleCoreEpId()); // 在卖家平台商的余额
                        accountDto.setMoney((salePrice - totalInPrice) * quantity); // 金额 卖价 * 票数
                        accountDto.setSubtractProfit(totalAddProfit * quantity); // 卖家每天的总利润 * 票数
                        accountDto.setAddProfit(totalProfit * quantity); // 买家每天的总利润 * 票数
                        accountDto.setOrderItemId(itemId);
                        accountDto.setSubtractData(saleAccountDataDtoList); // 卖家每天的单价利润
                        accountDto.setAddData(dataDtos); // 买家每天的单价利润
                    }
                    accounts.addAll(generateAccount(accountDto));
                }
            } else {
                // 平台内部企业分账(利润)
                OrderItemAccount account = new OrderItemAccount();
                account.setEpId(epId);
                account.setCoreEpId(coreEpId);
                account.setMoney(totalProfit * quantity);
                account.setProfit(totalProfit * quantity);
                account.setOrderItemId(itemId);
                account.setData(JsonUtils.toJson(dataDtos));
                account.setSettledMoney(0);
                account.setStatus(OrderConstant.AccountSplitStatus.NOT);
                orderItemAccountMapper.insertSelective(account);
                accounts.add(account);
                Integer val = coreSubMap.get(coreEpId);
                coreSubMap.put(coreEpId, val == null ? account.getMoney() : val + account.getMoney());
            }
        }
        for (Integer id : coreSubMap.keySet()) {
            List<AccountDataDto> dataDtos = daysAccountDataMap.get(id);
            OrderItemAccount account = new OrderItemAccount();
            account.setEpId(id);
            account.setCoreEpId(id);
            account.setMoney(-coreSubMap.get(id));
            account.setProfit(getTotalProfit(dataDtos) * quantity);
            account.setOrderItemId(itemId);
            account.setData(JsonUtils.toJson(dataDtos));
            account.setSettledMoney(0);
            account.setStatus(OrderConstant.AccountSplitStatus.NOT);
            orderItemAccountMapper.insertSelective(account);
            accounts.add(account);
        }
        return accounts;
    }

    private AccountDataDto addDayAccount(Map<Integer, AccountDataDto> dayAccountDataMap, List<EpSalesInfo> infoList, Integer epId, Date day) {
        AccountDataDto accountDataDto = dayAccountDataMap.get(epId);
        if (accountDataDto == null) {
            accountDataDto = getProfit(infoList, epId);
            accountDataDto.setDay(day);
            dayAccountDataMap.put(epId, accountDataDto);
        }
        return accountDataDto;
    }

    private int getTotalProfit(List<AccountDataDto> dataDtos) {
        int totalAddProfit = 0;
        // 卖家平台商每天的利润
        for (AccountDataDto dataDto : dataDtos) {
            totalAddProfit += dataDto.getProfit();
        }
        return totalAddProfit;
    }

    /**
     * 同步创建订单数据
     * @param orderId 订单ID
     */
    public void syncCreateOrderData(int orderId) {

        generateSyncByOrder(orderId)
                // 同步订单表
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                // 同步子订单表
                .put("t_order_item", orderItemMapper.selectByOrderId(orderId))
                // 同步子订单明细表
                .put("t_order_item_detail", orderItemDetailMapper.selectByOrderId(orderId))
                // 同步分账表
                .put("t_order_item_account", orderItemAccountMapper.selectByOrder(orderId))
                // 同步联系人表
                .put("t_shipping", CommonUtil.oneToList(shippingMapper.selectByOrder(orderId)))
                // 同步游客信息表
                .put("t_visitor", visitorMapper.selectByOrder(orderId))
                // 同步
                .sync();
    }

    /**
     * 同步订单审核通过数据
     * @param orderId 订单ID
     */
    public void syncOrderAuditAcceptData(int orderId, int orderItemId) {
        generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(orderItemId)))
                .sync();
    }

    /**
     * 同步订单支付数据
     * @param orderId 订单ID
     */
    public void syncOrderPaymentData(int orderId) {
        generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .sync();
    }

    /**
     * 同步订单分账数据
     * @param itemId 子订单ID
     */
    public void syncOrderAccountData(int itemId) {
        generateSyncByItem(itemId)
                .put("t_order_item_account", orderItemAccountMapper.selectByOrderItem(itemId))
                .sync();
    }

    /**
     * 同步发票数据
     * @param itemId 子订单ID
     */
    public void syncSendTicketData(int itemId) {
        generateSyncByItem(itemId)
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(itemId)))
                .put("t_ma_send_response", maSendResponseMapper.selectByOrderItemId(itemId))
                .sync();
    }

    /**
     * 同步消费数据
     * @param itemId 子订单ID
     */
    public void syncConsumeData(int itemId, String sn) {
        generateSyncByItem(itemId)
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                .put("t_order_clearance_serial", CommonUtil.oneToList(orderClearanceSerialMapper.selectBySn(sn)))
                .put("t_order_clearance_detail", CommonUtil.oneToList(orderClearanceDetailMapper.selectBySn(sn)))
                .put("t_visitor", visitorMapper.selectByOrderItem(itemId))
                .sync();
    }

    /**
     * 同步冲正数据
     * @param itemId 子订单ID
     */
    public void syncReConsumeData(int itemId, String sn) {
        generateSyncByItem(itemId)
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                .put("t_clearance_washed_serial", CommonUtil.oneToList(clearanceWashedSerialMapper.selectBySn(sn)))
                .put("t_visitor", visitorMapper.selectByOrderItem(itemId))
                .sync();
    }

    /**
     * 同步支付成功数据
     * @param orderId 订单ID
     */
    public void syncPaymentSuccessData(int orderId) {
        generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", orderItemMapper.selectByOrderId(orderId))
                .put("t_order_item_account", orderItemAccountMapper.selectByOrder(orderId))
                .sync();
    }

    /**
     * 同步反核销分账数据
     * @param itemId 子订单ID
     */
    public void syncReConsumeSplitAccountData(int itemId) {
        generateSyncByItem(itemId)
                .put("t_order_item_account", orderItemAccountMapper.selectByOrderItem(itemId))
                .sync();
    }
}
