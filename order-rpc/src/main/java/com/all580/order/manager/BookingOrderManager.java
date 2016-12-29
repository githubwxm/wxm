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
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
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
    @Autowired
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private GroupConsumeMapper groupConsumeMapper;

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
            if (maxCount > 0) {
                int count = getOrderByCount(productSubCode, sid, bookingDate);
                if (count >= maxCount) {
                    return new Result<>(false, Result.PARAMS_ERROR,
                            String.format("身份证:%s超出该产品当天最大订单次数,已定次数:%d,最大次数:%d",
                                    sid, count, maxCount));
                }
            }
            if (maxQuantity > 0) {
                Integer qty = CommonUtil.objectParseInteger(visitorMap.get("quantity"));
                if (qty <= 0) {
                    return new Result<>(false, Result.PARAMS_ERROR,
                            String.format("身份证:%s预定张数%d 不能小于1",
                                    sid, qty));
                }
                int quantity = getOrderByQuantity(productSubCode, sid, bookingDate);
                if (quantity + qty > maxQuantity) {
                    return new Result<>(false, Result.PARAMS_ERROR,
                            String.format("身份证:%s超出该产品当天最大购票数,已定张数%d,最大购票张数%d",
                                    sid, quantity, maxQuantity));
                }
            }
            sids.add(sid);
        }
        return new Result(true);
    }

    /**
     * 团队游客实名制信息验证
     * @param visitors 游客
     * @param realName 实名制
     * @param quantity 票数
     * @return
     */
    public Result<List<GroupMember>> validateGroupVisitor(List visitors, Integer realName, int quantity, int groupId) {
        List<GroupMember> members = null;
        List<Integer> ids = new ArrayList<>();
        if (visitors != null) {
            // 获取团队所有成员 如果这里不判断的话 可以不查 减轻压力
            for (Object o : visitors) {
                Integer memberId = CommonUtil.objectParseInteger(o);
                if (memberId == null) {
                    return new Result<>(false, Result.PARAMS_ERROR, "团队成员不能为空");
                }
                boolean have = false;
                for (Integer id : ids) {
                    if (id.intValue() == memberId) {
                        have = true;
                        break;
                    }
                }
                if (!have) {
                    ids.add(memberId);
                }
            }
            if (ids.size() > 0) {
                members = groupMemberMapper.selectByIds(groupId, ids);
            }
        }
        if (realName != null && realName != ProductConstants.NeedRealNameState.NO_NEED) {
            if (members == null || members.isEmpty() || members.size() != ids.size()) {
                return new Result<>(false, Result.PARAMS_ERROR, "团队成员不匹配");
            }

            if (realName == ProductConstants.NeedRealNameState.MUST && quantity != ids.size()) {
                return new Result<>(false, Result.PARAMS_ERROR, "一证一票信息不符");
            }
            if (realName == ProductConstants.NeedRealNameState.MUCH && ids.size() == 0) {
                return new Result<>(false, Result.PARAMS_ERROR, "一证多票信息不符");
            }
        }
        Result<List<GroupMember>> result = new Result<>(true);
        result.put(members);
        return result;
    }

    /**
     * 预定时间现在
     * @param bookingDate 预定时间
     * @param dayInfoList
     */
    public void validateBookingDate(Date bookingDate, List<ProductSalesDayInfo> dayInfoList) {
        Date when = new Date();
        for (ProductSalesDayInfo dayInfo : dayInfoList) {
            if (dayInfo.isBooking_limit()) {
                int dayLimit = dayInfo.getBooking_day_limit();
                Date limit = DateUtils.addDays(bookingDate, -dayLimit);
                try {
                    String time = dayInfo.getBooking_time_limit();
                    if (time != null) {
                        String[] timeArray = time.split(":");
                        limit = DateUtils.setHours(limit, Integer.parseInt(timeArray[0]));
                        limit = DateUtils.setMinutes(limit, Integer.parseInt(timeArray[1]));
                    }
                } catch (Exception e) {
                    throw new ApiException("预定时间限制数据不合法", e);
                }
                if (when.after(limit)) {
                    throw new ApiException("预定时间限制,最晚预定时间:" + DateFormatUtils.parseDateToDatetimeString(limit));
                }
            }
        }
    }

    /**
     * 判断身份证订单次数是否超过最大次数
     * @param productSubCode 子产品ID
     * @param sid 身份证
     * @param date 预定日期
     * @return
     */
    private int getOrderByCount(Long productSubCode, String sid, Date date) {
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        return orderItemMapper.countBySidAndProductForDate(productSubCode, sid, start, end);
    }

    /**
     * 判断身份证订票张数是否超过最大张数
     * @param productSubCode 子产品CODE
     * @param sid 身份证
     * @param date 预定日期
     * @return
     */
    private int getOrderByQuantity(Long productSubCode, String sid, Date date) {
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        return visitorMapper.quantityBySidAndProductForDate(productSubCode, sid, start, end);
    }

    /**
     * 创建订单生成订单数据
     * @param coreEpId 操作平台商
     * @param buyEpId 销售企业ID
     * @param buyEpName 下单企业名称
     * @param userId 销售用户ID
     * @param userName 销售用户名称
     * @param from 来源
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Order generateOrder(Integer coreEpId, Integer buyEpId, String buyEpName, Integer userId, String userName, Integer from, String remark) {
        Order order = new Order();
        order.setNumber(UUIDGenerator.generateUUID());
        order.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
        order.setBuy_ep_id(buyEpId);
        order.setBuy_ep_name(buyEpName);
        order.setBuy_operator_id(userId);
        order.setBuy_operator_name(userName);
        order.setCreate_time(new Date());
        order.setFrom_type(from);
        order.setRemark(remark);
        order.setPayee_ep_id(coreEpId);
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
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderItem generateItem(ProductSalesInfo info, Date endTime, int saleAmount, Date bookingDate, int days, int orderId, int quantity, Integer proSubId, Integer groupId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setNumber(UUIDGenerator.generateUUID());
        orderItem.setStart(bookingDate);
        orderItem.setEnd(endTime);
        orderItem.setSale_amount(saleAmount); // 销售价
        orderItem.setDays(days);
        orderItem.setGroup_id(groupId == null ? 0 : groupId); // 散客为0
        orderItem.setOrder_id(orderId);
        orderItem.setPro_name(info.getProduct_name());
        orderItem.setPro_sub_name(info.getProduct_sub_name());
        orderItem.setPro_sub_number(info.getProduct_sub_code());
        orderItem.setPro_sub_id(proSubId);
        orderItem.setPro_type(info.getProduct_type());
        orderItem.setMa_product_id(info.getMa_product_id());
        orderItem.setQuantity(quantity);
        orderItem.setPayment_flag(info.getPay_type());
        orderItem.setStatus(OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
        orderItem.setSupplier_ep_id(info.getEp_id());
        orderItem.setSupplier_core_ep_id(getCoreEpId(getCoreEpId(info.getEp_id())));
        orderItem.setSupplier_phone(info.getPhone());
        orderItem.setLow_quantity(info.getLow_use_quantity());
        orderItem.setEp_ma_id(info.getEp_ma_id());
        orderItem.setPro_sub_ticket_type(info.getProduct_sub_ticket_type());
        orderItemMapper.insertSelective(orderItem);
        return orderItem;
    }

    /**
     * 生成分账（平账）
     * @param dto
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public List<OrderItemAccount> generateAccount(GenerateAccountDto dto) {
        List<OrderItemAccount> accounts = new ArrayList<>();
        OrderItemAccount subtractAccount = new OrderItemAccount();
        subtractAccount.setEp_id(dto.getSubtractEpId());
        subtractAccount.setCore_ep_id(dto.getSubtractCoreId());
        subtractAccount.setOrder_item_id(dto.getOrderItemId());
        subtractAccount.setMoney(-dto.getMoney());
        subtractAccount.setProfit(dto.getSubtractProfit());
        subtractAccount.setSettled_money(0);
        subtractAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
        subtractAccount.setData(dto.getSubtractData() == null ? null : JsonUtils.toJson(dto.getSubtractData()));
        accounts.add(subtractAccount);
        orderItemAccountMapper.insertSelective(subtractAccount);

        OrderItemAccount addAccount = new OrderItemAccount();
        addAccount.setEp_id(dto.getAddEpId());
        addAccount.setCore_ep_id(dto.getAddCoreId());
        addAccount.setOrder_item_id(dto.getOrderItemId());
        addAccount.setMoney(dto.getMoney());
        addAccount.setProfit(dto.getAddProfit());
        addAccount.setSettled_money(0);
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
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderItemDetail generateDetail(ProductSalesDayInfo info, int itemId, Date day, int quantity) {
        OrderItemDetail orderItemDetail = new OrderItemDetail();
        orderItemDetail.setDay(day);
        orderItemDetail.setQuantity(quantity);
        orderItemDetail.setCust_refund_rule(info.getCust_refund_rule()); // 销售方退货规则
        orderItemDetail.setSaler_refund_rule(info.getSaler_refund_rule()); // 供应方退货规则
        orderItemDetail.setOrder_item_id(itemId);
        orderItemDetail.setCreate_time(new Date());
        orderItemDetail.setDisable_day(info.getDisable_date());
        orderItemDetail.setDisable_week(info.getDisable_week());
        orderItemDetail.setUse_hours_limit(info.getUse_hours_limit());
        Date effectiveDate = orderItemDetail.getDay();
        // 产品说就用预定的时间,即使下单时间比预定时间大也取预定时间
        if (orderItemDetail.getUse_hours_limit() != null) {
            effectiveDate = DateUtils.addHours(effectiveDate, orderItemDetail.getUse_hours_limit());
        }
        orderItemDetail.setEffective_date(effectiveDate);
        Date expiryDate = null;
        if (info.getEffective_type() == ProductConstants.EffectiveValidType.DAY) {
            // 这里目前只做了门票的,默认结束日期就是当天的,酒店应该是第二天
            expiryDate = DateUtils.addDays(orderItemDetail.getDay(), info.getEffective_day() - 1);
        } else {
            expiryDate = info.getEffective_end_date();
        }
        expiryDate = DateUtils.setHours(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.HOUR_OF_DAY));
        expiryDate = DateUtils.setMinutes(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.MINUTE));
        expiryDate = DateUtils.setSeconds(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.SECOND));
        if (effectiveDate.after(expiryDate)) {
            throw new ApiException("该产品已过期");
        }
        // 不能购买已过销售计划的产品
        if (new Date().after(info.getEnd_time())) {
            throw new ApiException("预定时间已过期");
        }
        orderItemDetail.setExpiry_date(expiryDate);
        orderItemDetail.setRefund_quantity(0);
        orderItemDetail.setUsed_quantity(0);
        orderItemDetailMapper.insertSelective(orderItemDetail);
        return orderItemDetail;
    }

    /**
     * 创建子订单游客信息
     * @param v 游客参数
     * @param itemDetailId 子订单详情ID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Visitor generateVisitor(Map v, int itemDetailId) {
        Visitor visitor = new Visitor();
        visitor.setRef_id(itemDetailId);
        visitor.setName(CommonUtil.objectParseString(v.get("name")));
        visitor.setPhone(CommonUtil.objectParseString(v.get("phone")));
        visitor.setSid(CommonUtil.objectParseString(v.get("sid")));
        visitor.setQuantity(CommonUtil.objectParseInteger(v.get("quantity")));
        visitor.setCard_type(OrderConstant.CardType.ID);
        visitorMapper.insertSelective(visitor);
        return visitor;
    }

    /**
     * 创建子订单游客信息
     * @param member
     * @param itemDetailId
     * @param groupId
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Visitor generateGroupVisitor(GroupMember member, int itemDetailId, Integer groupId) {
        Visitor visitor = new Visitor();
        visitor.setRef_id(itemDetailId);
        visitor.setName(member.getName());
        visitor.setPhone(member.getPhone());
        visitor.setSid(member.getCard());
        visitor.setGroup_id(groupId);
        visitor.setCard_type(member.getCard_type());
        visitorMapper.insertSelective(visitor);
        return visitor;
    }

    /**
     * 创建订单联系人
     * @param shippingMap 联系人参数
     * @param orderId 订单ID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Shipping generateShipping(Map shippingMap, int orderId) {
        Shipping shipping = new Shipping();
        shipping.setOrder_id(orderId);
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
            if (excludes != null && ArrayUtils.indexOf(excludes, orderItem.getId()) >= 0) {
                continue;
            }
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算分销价格
     * @param allDaysSales
     * @param buyEpId
     * @param quantity
     * @param from
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int[] calcSalesPrice(List<List<EpSalesInfo>> allDaysSales, ProductSalesInfo salesInfo, int buyEpId, int quantity, int from) {
        int salePrice = 0;
        int buyingPrice = 0;
        int shopPrice = 0;
        for (List<EpSalesInfo> daySales : allDaysSales) {
            // 获取销售商价格
            EpSalesInfo info = getBuyingPrice(daySales, buyEpId);
            if (info == null) {
                throw new ApiException("非法购买:该销售商未销售本产品");
            }
            buyingPrice += info.getPrice() * quantity; // 计算进货价
            shopPrice += (info.getShop_price() == null ? 0 : info.getShop_price()) * quantity; // 计算门市价

            EpSalesInfo self = new EpSalesInfo();
            self.setSale_ep_id(buyEpId);
            self.setEp_id(-1);
            // 代收:叶子销售商以门市价卖出
            self.setPrice(from == OrderConstant.FromType.TRUST ? info.getShop_price() : info.getPrice());
            if (self.getPrice() == null) {
                self.setPrice(0);
            }
            // 判断最低零售价
            if (from == OrderConstant.FromType.TRUST && self.getPrice() < salesInfo.getMin_price()) {
                throw new ApiException(String.format("产品:%s不能低于最低零售价:%d,当前售卖价格:%d",
                        salesInfo.getProduct_sub_name(), salesInfo.getMin_price()/100, self.getPrice()/100));
            }
            // 判断最高市场价
            if (self.getPrice() > salesInfo.getMarket_price()) {
                throw new ApiException(String.format("产品:%s不能高于市场价:%d,当前售卖价格:%d",
                        salesInfo.getProduct_sub_name(), salesInfo.getMarket_price()/100, self.getPrice()/100));
            }
            daySales.add(self);
            salePrice += self.getPrice() * quantity;
        }
        return new int[]{salePrice, buyingPrice, shopPrice};
    }

    /**
     * 支付分账
     * @param orderItems 子订单
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void paySplitAccount(Order order, List<OrderItem> orderItems) {
        if (orderItems == null) {
            orderItems = orderItemMapper.selectByOrderId(order.getId());
        }
        // 遍历所有子订单封装分账数据统一分账
        List<BalanceChangeInfo> infoList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            List<OrderItemAccount> orderItemAccounts = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
            for (OrderItemAccount itemAccount : orderItemAccounts) {
                BalanceChangeInfo info = new BalanceChangeInfo();
                info.setEp_id(itemAccount.getEp_id());
                info.setCore_ep_id(itemAccount.getCore_ep_id());
                info.setBalance(itemAccount.getMoney());
                infoList.add(info);
                itemAccount.setStatus(OrderConstant.AccountSplitStatus.HAS);
                orderItemAccountMapper.updateByPrimaryKeySelective(itemAccount);
            }
        }
        // 调用分账
        Result<BalanceChangeRsp> result = changeBalances(PaymentConstant.BalanceChangeType.PAY_SPLIT, String.valueOf(order.getNumber()), infoList);
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
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public List<OrderItemAccount> preSplitAccount(List<List<EpSalesInfo>> daySalesList, int itemId, int quantity, int payType, Date bookingDate) {
        // 预分账记录
        List<OrderItemAccount> accounts = new ArrayList<>();
        // 缓存平台商ID
        Map<Integer, Integer> coreEpMap = new HashMap<>();
        // 每个企业所有天的利润
        Map<String, List<AccountDataDto>> daysAccountDataMap = new HashMap<>();
        // 叶子销售商最终卖价
        int salePrice = 0;
        // 遍历每天的销售链
        int i = 0;
        for (List<EpSalesInfo> infoList : daySalesList) {
            // 一天的利润单价
            Map<String, AccountDataDto> dayAccountDataMap = new HashMap<>();
            Date day = DateUtils.addDays(bookingDate, i);
            for (EpSalesInfo info : infoList) {
                Integer buyCoreEpId = null;
                // 叶子销售商门市价销售
                if (info.getEp_id() == -1) {
                    buyCoreEpId = getCoreEp(coreEpMap, info.getSale_ep_id());
                    salePrice = info.getPrice();
                } else {
                    buyCoreEpId = getCoreEp(coreEpMap, info.getEp_id());
                }
                Integer saleCoreEpId = getCoreEp(coreEpMap, info.getSale_ep_id());
                // 卖家 == 平台商 && 买家 == 平台商
                if (info.getEp_id() == buyCoreEpId.intValue() && saleCoreEpId.intValue() == info.getSale_ep_id()) {
                    AccountDataDto dto = addDayAccount(dayAccountDataMap, saleCoreEpId, buyCoreEpId, infoList, day);
                    dto.setSaleCoreEpId(saleCoreEpId);
                }

                // 买家平台商ID == 卖家平台商ID && 卖家ID != 卖家平台商ID
                if (buyCoreEpId.intValue() == saleCoreEpId/* && info.getSaleEpId() != saleCoreEpId*/) {
                    AccountDataDto dto = addDayAccount(dayAccountDataMap, info.getSale_ep_id(), info.getEp_id() == -1 ? info.getSale_ep_id() : info.getEp_id(), infoList, day);
                    dto.setSaleCoreEpId(saleCoreEpId);
                }
            }

            // 把一天的利润添加到所有天当中
            for (String key : dayAccountDataMap.keySet()) {
                List<AccountDataDto> accountDataDtoList = daysAccountDataMap.get(key);
                if (accountDataDtoList == null) {
                    accountDataDtoList = new ArrayList<>();
                    daysAccountDataMap.put(key, accountDataDtoList);
                }
                accountDataDtoList.add(dayAccountDataMap.get(key));
            }
            i++;
        }

        // 把每天的利润集合做分账
        // 平台商本企业内部分账一次扣款
        Map<Integer, Integer> coreSubMap = new HashMap<>();
        for (String key : daysAccountDataMap.keySet()) {
            String[] keyArray = key.split("#");
            Integer saleEpId = Integer.parseInt(keyArray[0]);
            Integer buyEpId = Integer.parseInt(keyArray[1]);
            Integer saleCoreEpId = getCoreEp(coreEpMap, saleEpId);
            Integer buyCoreEpId = getCoreEp(coreEpMap, buyEpId);

            List<AccountDataDto> dataDtoList = daysAccountDataMap.get(key);
            int totalOutPrice = 0;
            int totalProfit = 0;
            for (AccountDataDto dataDto : dataDtoList) {
                totalOutPrice += dataDto.getOutPrice();
                totalProfit += dataDto.getProfit();
            }
            // 平台商
            if (saleCoreEpId.equals(saleEpId) && buyCoreEpId.equals(buyEpId)) {
                // 预付
                if (payType == ProductConstants.PayType.PREPAY) {
                    OrderItemAccount addAccount = new OrderItemAccount();
                    addAccount.setEp_id(saleEpId);
                    addAccount.setSale_ep_id(getSaleEpId(daySalesList.get(0), saleEpId));
                    addAccount.setCore_ep_id(saleCoreEpId);
                    addAccount.setMoney(totalOutPrice * quantity);
                    addAccount.setProfit(totalOutPrice * quantity);
                    addAccount.setOrder_item_id(itemId);
                    addAccount.setData(JsonUtils.toJson(dataDtoList));
                    addAccount.setSettled_money(0);
                    addAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
                    orderItemAccountMapper.insertSelective(addAccount);
                    accounts.add(addAccount);

                    OrderItemAccount subAccount = new OrderItemAccount();
                    subAccount.setEp_id(buyEpId);
                    subAccount.setSale_ep_id(getSaleEpId(daySalesList.get(0), buyEpId));
                    subAccount.setCore_ep_id(saleCoreEpId);
                    subAccount.setMoney(-(totalOutPrice * quantity));
                    subAccount.setProfit(-(totalOutPrice * quantity));
                    subAccount.setOrder_item_id(itemId);
                    subAccount.setData(null);
                    subAccount.setSettled_money(0);
                    subAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
                    orderItemAccountMapper.insertSelective(subAccount);
                    accounts.add(subAccount);
                } else {
                    List<AccountDataDto> buyDataDtoList = getDataDtoList(daysAccountDataMap, buyEpId);
                    OrderItemAccount addAccount = new OrderItemAccount();
                    addAccount.setEp_id(buyEpId);
                    addAccount.setSale_ep_id(getSaleEpId(daySalesList.get(0), buyEpId));
                    addAccount.setCore_ep_id(saleCoreEpId);
                    addAccount.setMoney((salePrice - totalOutPrice) * quantity);
                    addAccount.setProfit(getTotalProfit(buyDataDtoList) * quantity);
                    addAccount.setOrder_item_id(itemId);
                    addAccount.setData(JsonUtils.toJson(buyDataDtoList));
                    addAccount.setSettled_money(0);
                    addAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
                    orderItemAccountMapper.insertSelective(addAccount);
                    accounts.add(addAccount);

                    OrderItemAccount subAccount = new OrderItemAccount();
                    subAccount.setEp_id(saleCoreEpId);
                    subAccount.setSale_ep_id(getSaleEpId(daySalesList.get(0), saleCoreEpId));
                    subAccount.setCore_ep_id(saleCoreEpId);
                    subAccount.setMoney(-((salePrice - totalOutPrice) * quantity));
                    subAccount.setProfit(totalProfit * quantity);
                    subAccount.setOrder_item_id(itemId);
                    subAccount.setData(JsonUtils.toJson(dataDtoList));
                    subAccount.setSettled_money(0);
                    subAccount.setStatus(OrderConstant.AccountSplitStatus.NOT);
                    orderItemAccountMapper.insertSelective(subAccount);
                    accounts.add(subAccount);
                }
            } else {
                // 平台内部企业分账(利润)
                if (!saleEpId.equals(saleCoreEpId)) {
                    OrderItemAccount account = new OrderItemAccount();
                    account.setEp_id(saleEpId);
                    account.setSale_ep_id(getSaleEpId(daySalesList.get(0), saleEpId));
                    account.setCore_ep_id(saleCoreEpId);
                    account.setMoney(totalProfit * quantity);
                    account.setProfit(totalProfit * quantity);
                    account.setOrder_item_id(itemId);
                    account.setData(JsonUtils.toJson(dataDtoList));
                    account.setSettled_money(0);
                    account.setStatus(OrderConstant.AccountSplitStatus.NOT);
                    orderItemAccountMapper.insertSelective(account);
                    accounts.add(account);
                    Integer val = coreSubMap.get(saleCoreEpId);
                    coreSubMap.put(saleCoreEpId, val == null ? account.getMoney() : val + account.getMoney());
                }
            }
        }
        for (Integer id : coreSubMap.keySet()) {
            List<AccountDataDto> dataDtoList = getDataDtoList(daysAccountDataMap, id);
            OrderItemAccount account = new OrderItemAccount();
            account.setEp_id(id);
            account.setSale_ep_id(getSaleEpId(daySalesList.get(0), id));
            account.setCore_ep_id(id);
            account.setMoney(-coreSubMap.get(id));
            account.setProfit(getTotalProfit(dataDtoList) * quantity);
            account.setOrder_item_id(itemId);
            account.setData(JsonUtils.toJson(dataDtoList));
            account.setSettled_money(0);
            account.setStatus(OrderConstant.AccountSplitStatus.NOT);
            orderItemAccountMapper.insertSelective(account);
            accounts.add(account);
        }
        return accounts;
    }

    private AccountDataDto addDayAccount(Map<String, AccountDataDto> dayAccountDataMap, Integer saleEpId, Integer buyEpId, List<EpSalesInfo> infoList, Date day) {
        String key = saleEpId + "#" + buyEpId;
        AccountDataDto accountDataDto = dayAccountDataMap.get(key);
        if (accountDataDto == null) {
            accountDataDto = getProfit(infoList, saleEpId);
            accountDataDto.setDay(day);
            dayAccountDataMap.put(key, accountDataDto);
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

    private int getTotalProfit(Map<String, List<AccountDataDto>> daysAccountDataMap, int epId) {
        return getTotalProfit(getDataDtoList(daysAccountDataMap, epId));
    }

    private List<AccountDataDto> getDataDtoList(Map<String, List<AccountDataDto>> daysAccountDataMap, int epId) {
        for (String key : daysAccountDataMap.keySet()) {
            if (key.startsWith(epId + "#")) {
                return daysAccountDataMap.get(key);
            }
        }
        return null;
    }

    /**
     * 同步创建订单数据
     * @param orderId 订单ID
     */
    public Map syncCreateOrderData(int orderId) {

        return generateSyncByOrder(orderId)
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
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步订单审核通过数据
     * @param orderId 订单ID
     */
    public Map syncOrderAuditAcceptData(int orderId, int orderItemId) {
        return generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(orderItemId)))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步订单支付数据
     * @param orderId 订单ID
     */
    public Map syncOrderPaymentData(int orderId) {
        return generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步订单分账数据
     * @param itemId 子订单ID
     */
    public Map syncOrderAccountData(int itemId) {
        return generateSyncByItem(itemId)
                .put("t_order_item_account", orderItemAccountMapper.selectByOrderItem(itemId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步发票数据
     * @param itemId 子订单ID
     */
    public Map syncSendTicketData(int itemId) {
        return generateSyncByItem(itemId)
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(itemId)))
                .put("t_ma_send_response", maSendResponseMapper.selectByOrderItemId(itemId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步消费数据
     * @param itemId 子订单ID
     */
    public Map syncConsumeData(int itemId, String sn) {
        return generateSyncByItem(itemId)
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                .put("t_order_clearance_serial", CommonUtil.oneToList(orderClearanceSerialMapper.selectBySn(sn)))
                .put("t_order_clearance_detail", CommonUtil.oneToList(orderClearanceDetailMapper.selectBySn(sn)))
                .put("t_visitor", visitorMapper.selectByOrderItem(itemId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步消费数据
     * @param itemId 子订单ID
     */
    public Map syncConsumeDataForGroup(int itemId, String sn) {
        return generateSyncByItem(itemId)
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                .put("t_order_clearance_serial", CommonUtil.oneToList(orderClearanceSerialMapper.selectBySn(sn)))
                .put("t_group_consume", groupConsumeMapper.selectBySn(sn))
                .put("t_visitor", visitorMapper.selectByOrderItem(itemId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步冲正数据
     * @param itemId 子订单ID
     */
    public Map syncReConsumeData(int itemId, String sn) {
        return generateSyncByItem(itemId)
                .put("t_order_item_detail", orderItemDetailMapper.selectByItemId(itemId))
                .put("t_clearance_washed_serial", CommonUtil.oneToList(clearanceWashedSerialMapper.selectBySn(sn)))
                .put("t_visitor", visitorMapper.selectByOrderItem(itemId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步支付成功数据
     * @param orderId 订单ID
     */
    public Map syncPaymentSuccessData(int orderId) {
        return generateSyncByOrder(orderId)
                .put("t_order", CommonUtil.oneToList(orderMapper.selectByPrimaryKey(orderId)))
                .put("t_order_item", orderItemMapper.selectByOrderId(orderId))
                .put("t_order_item_account", orderItemAccountMapper.selectByOrder(orderId))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步发票中数据
     * @param itemId 订单ID
     */
    public Map syncSendingData(int itemId) {
        return generateSyncByItem(itemId)
                .put("t_order_item", CommonUtil.oneToList(orderItemMapper.selectByPrimaryKey(itemId)))
                .sync().getDataMapForJsonMap();
    }

    /**
     * 同步反核销分账数据
     * @param itemId 子订单ID
     */
    public Map syncReConsumeSplitAccountData(int itemId) {
        return generateSyncByItem(itemId)
                .put("t_order_item_account", orderItemAccountMapper.selectByOrderItem(itemId))
                .sync().getDataMapForJsonMap();
    }
}
