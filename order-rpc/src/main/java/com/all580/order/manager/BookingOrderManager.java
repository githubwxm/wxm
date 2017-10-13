package com.all580.order.manager;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.AccountDataDto;
import com.all580.order.dto.PackageOrderDto;
import com.all580.order.dto.PackageOrderItemDto;
import com.all580.order.dto.PriceDto;
import com.all580.order.entity.*;
import com.all580.order.util.AccountUtil;
import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.Result;
import com.framework.common.event.MnsEventAspect;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import com.framework.common.outside.JobAspect;
import com.framework.common.outside.JobTask;
import com.framework.common.util.CommonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
    private GroupMemberMapper groupMemberMapper;
    @Autowired
    private OrderItemSalesChainMapper orderItemSalesChainMapper;
    @Autowired
    @Getter
    private MnsEventAspect eventManager;
    @Autowired
    private JobAspect jobManager;
    @Value("${resend_ticket_max_times}")
    private Integer resendTicketMax;
    @Value("${resend_ticket_interval}")
    private Integer resendTicketInterval;
    @Autowired
    private PackageOrderChainMapper packageOrderChainMapper;
    @Autowired
    private PackageOrderItemChainMapper packageOrderItemChainMapper;
    @Autowired
    private RefundOrderManager refundOrderManager;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void dealPackageOrderItemConSume(OrderItem orderItem){
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        if (order.getSource() == OrderConstant.OrderSourceType.SOURCE_TYPE_SYS){
            //套票元素订单核销
            PackageOrderItemDto packageOrderItemDto = this.getOrderItemChainForPackage(orderItem, Boolean.TRUE);
            while (packageOrderItemDto != null){
                List<OrderItem> packageOrderItems = packageOrderItemDto.getPackageOrderItems();
                //所有子订单票据已使用，则套票订单已使用
                Boolean isUsed = Boolean.TRUE;
                for (OrderItem item : packageOrderItems) {
                    //使用数等于预定数，则已使用
                    isUsed = item.getQuantity().intValue() == item.getUsed_quantity() && isUsed;
                }
                if (isUsed){
                    //设置套票订单使用数
                    packageOrderItemDto.setUsed_quantity(packageOrderItemDto.getQuantity());
                    orderItemMapper.updateByPrimaryKeySelective(packageOrderItemDto);
                }
                packageOrderItemDto = this.getOrderItemChainForPackage(packageOrderItemDto, Boolean.TRUE);
            }
        }
    }

    /**
     * 逐级处理套票关联子订单的出票状态
     * @param orderItem
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void checkTicketOrderItemChainForPackage(OrderItem orderItem){
        //获取元素子订单的上一层子订单
        PackageOrderItemDto packageOrderItemDto = this.getOrderItemChainForPackage(orderItem, Boolean.TRUE);
        while (packageOrderItemDto != null){
            List<OrderItem> orderItemList = packageOrderItemDto.getPackageOrderItems();
            for (OrderItem item : orderItemList) {
                if (item.getStatus() == OrderConstant.OrderItemStatus.NON_SEND){
                    //元素订单未出票
                    packageOrderItemDto.setStatus(OrderConstant.OrderItemStatus.TICKETING);
                    break;
                }
                if (item.getStatus() == OrderConstant.OrderItemStatus.TICKET_FAIL){
                    //元素订单出片失败
                    packageOrderItemDto.setStatus(OrderConstant.OrderItemStatus.TICKET_FAIL);
                    break;
                }
                if (item.getStatus() == OrderConstant.OrderItemStatus.TICKETING){
                    //元素订单出票中
                    packageOrderItemDto.setStatus(OrderConstant.OrderItemStatus.TICKETING);
                    break;
                }
                if (item.getStatus() == OrderConstant.OrderItemStatus.SEND){
                    //元素订单已出票
                    packageOrderItemDto.setStatus(OrderConstant.OrderItemStatus.SEND);
                }
            }
            //更改套票子订单出票状态
            orderItemMapper.updateByPrimaryKeySelective(packageOrderItemDto);
            //套票出票成功
            if (packageOrderItemDto.getStatus() == OrderConstant.OrderItemStatus.SEND){
                //记录出票日志
                log.info(OrderConstant.LogOperateCode.NAME, this.orderLog(null, packageOrderItemDto.getId(),
                        0, "ORDER_EVENT", OrderConstant.LogOperateCode.SENDED,
                        packageOrderItemDto.getQuantity() * packageOrderItemDto.getDays(), "已出票", null));
            }
            packageOrderItemDto = this.getOrderItemChainForPackage(packageOrderItemDto, Boolean.TRUE);
        }
    }

    /**
     * 对于套票订单审核不通过状态的处理，记录子订单审核结果
     * @param item
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void dealAuditFailOrderItemChainForPackage(OrderItem item){
        OrderItem orderItem = this.getOrderItemChainForPackage(item, Boolean.FALSE);
        Integer packageOrderId = null;
        while (orderItem != null){
            orderItem.setAudit_time(new Date());
            orderItem.setAudit(Boolean.FALSE);
            orderItemMapper.updateByPrimaryKeySelective(orderItem);
            //记录套票订单id
            packageOrderId = orderItem.getOrder_id();
            orderItem = this.getOrderItemChainForPackage(orderItem, Boolean.FALSE);
        }
        //审核不通过，取消套票订单
        Order order = orderMapper.selectByPrimaryKey(packageOrderId);
        refundOrderManager.cancel(order);
    }

    /**
     * 逐级处理套票关联订单的审核状态
     * @param orders
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void checkAuditOrderChainForPackage(Order... orders){
        //获取元素订单的上一层的订单
        List<PackageOrderDto> packageOrderDtoList = this.getOrderChainForPackage(Arrays.asList(orders));
        //递归调用
        while (!CollectionUtils.isEmpty(packageOrderDtoList)){
            for (PackageOrderDto packageOrderDto : packageOrderDtoList) {
                List<Order> itemOrders = packageOrderDto.getPackageItemOrders();
                boolean isAudit = Boolean.TRUE;
                for (Order itemOrder : itemOrders) {
                    //如果套票有元素订单待审核
                    if (itemOrder.getStatus() == OrderConstant.OrderStatus.AUDIT_WAIT){
                        isAudit = Boolean.FALSE;
                        break;
                    }
                }
                if (isAudit){
                    packageOrderDto.setStatus(OrderConstant.OrderStatus.PAY_WAIT);
                    packageOrderDto.setAudit_time(new Date());
                }else {
                    packageOrderDto.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                }
                //修改审核状态
                orderMapper.updateByPrimaryKeySelective(packageOrderDto);
            }
            packageOrderDtoList = this.getOrderChainForPackage(packageOrderDtoList);
        }
    }

    /**
     * 获取套票元素子订单的上一层子订单
     * @param orderItem
     * @param fetchItem 是否子订单包其所关联的所有元素子订单
     * @return
     */
    public PackageOrderItemDto getOrderItemChainForPackage(OrderItem orderItem, boolean fetchItem){
        PackageOrderItemDto packageOrderItemDto = orderItemMapper.selectPackageOrderItem(orderItem);
        if (fetchItem && packageOrderItemDto != null){
            packageOrderItemDto.setPackageOrderItems(orderItemMapper.selectOrderItemsForPackageOrder(packageOrderItemDto.getId()));
        }
        return packageOrderItemDto;
    }

    /**
     * 获取套票元素订单的上一层订单（每个订单包其所关联的所有元素订单）
     * @param orders
     * @return
     */
    public List<PackageOrderDto> getOrderChainForPackage(List<? extends Order> orders){
        return orderMapper.selectPackageOrder(orders);
    }

    /**
     * 套票打包创建订单参数组装
     * @param params
     * @param orderItem
     * @param itemMap
     * @return
     */
    public Map gerneratePackageItemParams(Map<String, Object> params, OrderItem orderItem, Map<String, List<Map>> itemMap){
        //由打包商去下单购买元素产品
        params.put("ep_id", orderItem.getSupplier_ep_id());
        params.put("core_ep_id", orderItem.getSupplier_core_ep_id());
        params.put("remark", "套票元素自动下单");
        params.put("operator_id", 0);
        params.put("operator_name", OrderConstant.CREATE_ADAPTER);
        params.put("source", OrderConstant.OrderSourceType.SOURCE_TYPE_WEB);
        params.put("product_type", ProductConstants.ProductType.PACKAGE);
        params.put("source", OrderConstant.OrderSourceType.SOURCE_TYPE_SYS);

        params.put("items", itemMap.get(String.valueOf(orderItem.getPro_sub_number())));

        return params;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public int insertPackageOrderChain(int orderId, int refOrderId){
        PackageOrderChain orderChain = new PackageOrderChain();
        orderChain.setOrder_id(orderId);
        orderChain.setRef_order_id(refOrderId);
        return packageOrderChainMapper.insertSelective(orderChain);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void insertPackageOrderItemChain(OrderItem orderItem, List<OrderItem> orderItems){
        for (OrderItem item : orderItems) {
            PackageOrderItemChain packageOrderItemChain = new PackageOrderItemChain();
            packageOrderItemChain.setOrder_item_id(orderItem.getId());
            packageOrderItemChain.setRef_item_id(item.getId());
            packageOrderItemChainMapper.insertSelective(packageOrderItemChain);
        }
    }

    /**
     * 验证游客信息
     *
     * @param visitors       游客信息
     * @param productSubCode 子产品CODE
     * @param bookingDate    预定时间
     * @param maxCount       最大次数
     * @param maxQuantity    最大张数
     * @return
     */
    public int validateVisitor(List<Map> visitors, Long productSubCode, Date bookingDate, Integer maxCount, Integer maxQuantity) {
        int total = 0;
        Set<String> sids = new HashSet<>();
        for (Map visitorMap : visitors) {
            String sid = CommonUtil.objectParseString(visitorMap.get("sid"));
            if (sids.contains(sid)) {
                throw new ApiException("身份证:" + sid + "重复");
            }
            if (maxCount != null && maxCount > 0) {
                int count = getOrderByCount(productSubCode, sid, bookingDate);
                if (count >= maxCount) {
                    throw new ApiException(String.format("身份证:%s超出该产品当天最大订单次数,已定次数:%d,最大次数:%d",
                            sid, count, maxCount))
                            .dataMap().putData("idCard", sid).putData("booking", count).putData("max", maxCount);
                }
            }
            Integer qty = CommonUtil.objectParseInteger(visitorMap.get("quantity"));
            if (qty <= 0) {
                throw new ApiException(String.format("身份证:%s预定张数%d 不能小于1", sid, qty));
            }
            total += qty;
            if (maxQuantity != null && maxQuantity > 0) {
                int quantity = getOrderByQuantity(productSubCode, sid, bookingDate);
                if (quantity + qty > maxQuantity) {
                    throw new ApiException(String.format("身份证:%s超出该产品当天最大购票数,已定张数%d,最大购票张数%d",
                            sid, quantity, maxQuantity))
                            .dataMap().putData("idCard", sid).putData("booking", quantity).putData("max", maxQuantity);
                }
            }
            sids.add(sid);
        }
        return total;
    }

    /**
     * 团队游客实名制信息验证
     *
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
     *
     * @param bookingDate 预定时间
     * @param salesInfo
     */
    public void validateBookingDate(Date bookingDate, ProductSalesInfo salesInfo) {
        Date when = new Date();
        for (ProductSalesDayInfo dayInfo : salesInfo.getDay_info_list()) {
            if (dayInfo.isBooking_limit()) {
                int dayLimit = dayInfo.getBooking_day_limit();
                Date limit = DateUtils.addDays(bookingDate, -dayLimit);
                String time = null;
                try {
                    time = dayInfo.getBooking_time_limit();
                    if (time != null) {
                        String[] timeArray = time.split(":");
                        limit = DateUtils.setHours(limit, Integer.parseInt(timeArray[0]));
                        limit = DateUtils.setMinutes(limit, Integer.parseInt(timeArray[1]));
                    }
                } catch (Exception e) {
                    throw new ApiException(salesInfo.getProduct_sub_name() + "预定时间限制数据不合法", e);
                }
                if (when.after(limit)) {
                    throw new ApiException(String.format(salesInfo.getProduct_sub_name() + "产品有预订限制，需在订购时间的前%d天的%s前预订", dayLimit, time == null ? "00:00" : time));
                }
            }
        }
    }

    /**
     * 判断身份证订单次数是否超过最大次数
     *
     * @param productSubCode 子产品ID
     * @param sid            身份证
     * @param date           预定日期
     * @return
     */
    private int getOrderByCount(Long productSubCode, String sid, Date date) {
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        return orderItemMapper.countBySidAndProductForDate(productSubCode, sid, start, end);
    }

    /**
     * 判断身份证订票张数是否超过最大张数
     *
     * @param productSubCode 子产品CODE
     * @param sid            身份证
     * @param date           预定日期
     * @return
     */
    private int getOrderByQuantity(Long productSubCode, String sid, Date date) {
        Date start = DateFormatUtils.dayBegin(date);
        Date end = DateFormatUtils.dayEnd(date);
        return visitorMapper.quantityBySidAndProductForDate(productSubCode, sid, start, end);
    }

    /**
     * 创建订单生成订单数据
     *
     * @param coreEpId  操作平台商
     * @param buyEpId   销售企业ID
     * @param buyEpName 下单企业名称
     * @param userId    销售用户ID
     * @param userName  销售用户名称
     * @param from      来源
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Order generateOrder(Integer coreEpId, Integer buyEpId, String buyEpName, Integer userId, String userName, Integer from, String remark, String outerId, Integer source) {
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
        order.setOuter_id(StringUtils.isEmpty(outerId) ? "_" + order.getNumber() : outerId);
        order.setSource(source);
        orderMapper.insertSelective(order);
        return order;
    }

    /**
     * 创建子订单生成子订单数据
     *
     * @param info       产品信息
     * @param saleAmount 进货价
     * @param days       天数
     * @param orderId    订单ID
     * @param quantity   张数
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderItem generateItem(ProductSalesInfo info, Date endTime, int saleAmount, Date bookingDate, int days,
                                  int orderId, int quantity, Integer groupId, String memo, boolean isSend) {
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
        orderItem.setPro_sub_id(info.getProduct_sub_id());
        orderItem.setPro_type(info.getProduct_type());
        orderItem.setMa_product_id(info.getMa_product_id());
        orderItem.setQuantity(quantity);
        orderItem.setPayment_flag(info.getPay_type());
        orderItem.setStatus(info.getProduct_type() == ProductConstants.ProductType.PACKAGE ?
                OrderConstant.OrderItemStatus.AUDIT_WAIT : OrderConstant.OrderItemStatus.AUDIT_SUCCESS);
        orderItem.setSupplier_ep_id(info.getEp_id());
        orderItem.setSupplier_core_ep_id(getCoreEpId(getCoreEpId(info.getEp_id())));
        orderItem.setSupplier_phone(info.getPhone());
        orderItem.setLow_quantity(info.getLow_use_quantity());
        orderItem.setEp_ma_id(info.getEp_ma_id());
        orderItem.setPro_sub_ticket_type(info.getProduct_sub_ticket_type());
        orderItem.setMemo(memo);
        orderItem.setResend_max_times(resendTicketMax);
        orderItem.setResend_interval(resendTicketInterval);
        orderItem.setLast_resend_time(new Date());
        orderItem.setVoucher_msg(info.getVoucher_msg());
        orderItem.setVoucher_template(info.getVoucher_template());
        orderItem.setTicket_msg(info.getTicket_msg());
        orderItem.setSend(isSend);
        orderItemMapper.insertSelective(orderItem);
        return orderItem;
    }

    /**
     * 创建子订单详情
     *
     * @param info     产品信息
     * @param item   子订单ID
     * @param day      当天
     * @param quantity 张数
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderItemDetail generateDetail(ProductSalesDayInfo info, OrderItem item, Date day, int quantity, Integer lowQuantity, Integer supplyPrice, Integer salePrice) {
        OrderItemDetail orderItemDetail = new OrderItemDetail();
        orderItemDetail.setSettle_price(info.getSettle_price());
        orderItemDetail.setSupply_price(supplyPrice);
        orderItemDetail.setSale_price(salePrice);
        orderItemDetail.setDay(day);
        orderItemDetail.setQuantity(quantity);
        orderItemDetail.setCust_refund_rule(info.getCust_refund_rule()); // 销售方退货规则
        orderItemDetail.setSaler_refund_rule(info.getSaler_refund_rule()); // 供应方退货规则
        orderItemDetail.setOrder_item_id(item.getId());
        Date date = new Date();
        orderItemDetail.setCreate_time(date);
        orderItemDetail.setLow_quantity(lowQuantity);
        orderItemDetail.setDisable_day(info.getDisable_date());
        orderItemDetail.setDisable_week(info.getDisable_week());
        orderItemDetail.setUse_hours_limit(info.getUse_hours_limit());
        Date effectiveDate = orderItemDetail.getDay();
        Date expiryDate = null;
        if (info.getEffective_type() != null){
            if (info.getEffective_type() == ProductConstants.EffectiveValidType.DAY) {
                // 产品说就用预定的时间,即使下单时间比预定时间大也取预定时间
                effectiveDate = orderItemDetail.getUse_hours_limit() != null ? DateUtils.addHours(effectiveDate, orderItemDetail.getUse_hours_limit()) : effectiveDate;
                // 这里目前只做了门票的,默认结束日期就是当天的,酒店应该是第二天
                expiryDate = DateUtils.addDays(orderItemDetail.getDay(), info.getEffective_day() - 1);
                expiryDate = DateUtils.setHours(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.HOUR_OF_DAY));
                expiryDate = DateUtils.setMinutes(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.MINUTE));
                expiryDate = DateUtils.setSeconds(expiryDate, DateFormatUtils.get(info.getEnd_time(), Calendar.SECOND));
            } else {
                effectiveDate = date.after(info.getEffective_start_date()) ? (orderItemDetail.getUse_hours_limit() != null ? DateUtils.addHours(date, orderItemDetail.getUse_hours_limit()) : date) : info.getEffective_start_date();
                expiryDate = info.getEffective_end_date();
            }
            if (effectiveDate.after(expiryDate)) {
                throw new ApiException("该产品已过期");
            }
            // 不能购买已过销售计划的产品
            if (date.after(info.getEnd_time())) {
                throw new ApiException("您订购的产品[" + item.getPro_sub_name() + "]预定时间已过期");
            }
            orderItemDetail.setEffective_date(effectiveDate);
            orderItemDetail.setExpiry_date(expiryDate);
        }
        orderItemDetail.setRefund_quantity(0);
        orderItemDetail.setUsed_quantity(0);
        orderItemDetailMapper.insertSelective(orderItemDetail);
        return orderItemDetail;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public OrderItemSalesChain generateChain(OrderItem item, Integer epId, Date day, List<EpSalesInfo> daySales) {
        AccountDataDto dataDto = AccountUtil.generateAccountData(daySales, epId, day);
        OrderItemSalesChain chain = new OrderItemSalesChain();
        chain.setOrder_item_id(item.getId());
        chain.setDay(day);
        chain.setEp_id(epId);
        chain.setCore_ep_id(getCoreEpId(getCoreEpId(epId)));
        chain.setSale_ep_id(dataDto.getSaleEpId());
        chain.setSale_core_ep_id(getCoreEpId(getCoreEpId(dataDto.getSaleEpId())));
        chain.setIn_price(dataDto.getInPrice());
        chain.setOut_price(dataDto.getOutPrice());
        orderItemSalesChainMapper.insertSelective(chain);
        return chain;
    }

    /**
     * 创建子订单游客信息
     *
     * @param v            游客参数
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
        visitor.setGroup_id(CommonUtil.objectParseInteger(v.get("group_id")));
        visitor.setQuantity(CommonUtil.objectParseInteger(v.get("quantity")));
        visitor.setCard_type(OrderConstant.CardType.ID);
        visitor.setSex(CommonUtil.objectParseInteger(v.get("sex")));
        visitorMapper.insertSelective(visitor);
        return visitor;
    }

    /**
     * 创建子订单游客信息
     *
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
     *
     * @param shippingMap 联系人参数
     * @param orderId     订单ID
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

    public Order selectByOuter(Integer epId, String outerId) {
        return orderMapper.selectByOuter(epId, outerId);
    }

    public List<OrderItem> selectByOrder(Integer orderId) {
        return orderItemMapper.selectByOrderId(orderId);
    }

    /**
     * 判断所有子订单是否审核通过
     *
     * @param orderId  订单ID
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
     *
     * @param allDaysSales
     * @param buyEpId
     * @param quantity
     * @param from
     * @return {0:销售价,1:进货价,2:门市价}
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public PriceDto calcSalesPrice(List<List<EpSalesInfo>> allDaysSales, ProductSalesInfo salesInfo, int buyEpId, int quantity, int from) {
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
            self.setEp_id(buyEpId);
            switch (from) {
                // 代收:叶子销售商以门市价卖出
                case OrderConstant.FromType.TRUST:
                    if (!info.isRetail()) throw new ApiException("该产品未上架零售");
                    if (info.getShop_price() == null || info.getShop_price() < info.getPrice()) {
                        throw new ApiException("零售价不能小于结算价");
                    }
                    self.setPrice(info.getShop_price());
                    break;
                // 最低售价:叶子销售商以产品最低售价卖出
                case OrderConstant.FromType.MINIMUM_SALE:
                    if (info.getPrice() > salesInfo.getMin_price()) throw new ApiException("供应价不能大于最低售价");
                    self.setPrice(salesInfo.getMin_price());
                    break;
                default:
                    self.setPrice(info.getPrice());
            }
            if (self.getPrice() == null) {
                self.setPrice(0);
            }
            /*// 判断最低零售价
            if (from == OrderConstant.FromType.TRUST && self.getPrice() < salesInfo.getMin_price()) {
                throw new ApiException(String.format("产品:%s不能低于最低零售价:%f,当前售卖价格:%f",
                        salesInfo.getProduct_sub_name(), Arith.round(salesInfo.getMin_price()/100f, 2), Arith.round(self.getPrice()/100f, 2)))
                        .dataMap().putData("min", salesInfo.getMin_price()).putData("current", self.getPrice());
            }
            // 判断最高市场价
            if (self.getPrice() > salesInfo.getMarket_price()) {
                throw new ApiException(String.format("产品:%s不能高于市场价:%f,当前售卖价格:%f",
                        salesInfo.getProduct_sub_name(), Arith.round(salesInfo.getMarket_price()/100f, 2), Arith.round(self.getPrice()/100f, 2)))
                        .dataMap().putData("market", salesInfo.getMarket_price()).putData("current", self.getPrice());
            }*/
            daySales.add(self);
            salePrice += self.getPrice() * quantity;
        }
        return new PriceDto(salePrice, buyingPrice, shopPrice);
    }

    /**
     * 支付预分账
     * @param daySalesList 每天销售链
     * @param orderItem 子订单
     * @param finalEpId 最终售卖企业
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public List<OrderItemAccount> prePaySplitAccount(List<List<EpSalesInfo>> daySalesList, OrderItem orderItem, int finalEpId) {
        Map<Integer, Collection<AccountDataDto>> salesMap = AccountUtil.parseEpSales(daySalesList, orderItem.getStart());
        AccountUtil.setAccountDataCoreEpId(getCoreEpIds(salesMap.keySet()), salesMap);
        List<OrderItemAccount> accounts = AccountUtil.paySplitAccount(salesMap, orderItem, finalEpId);
        for (OrderItemAccount account : accounts) {
            orderItemAccountMapper.insertSelective(account);
        }
        return accounts;
    }

    /**
     * 组装支付分账信息
     * @param order
     */
    public List<BalanceChangeInfo> packagingPaySplitAccount(Order order) {
        return packagingPaySplitAccount(order, orderItemMapper.selectByOrderId(order.getId()));
    }

    /**
     * 组装支付分账信息
     * @param order
     */
    public List<BalanceChangeInfo> packagingPaySplitAccount(Order order, List<OrderItem> orderItems) {
        List<BalanceChangeInfo> balanceChangeInfoList = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            List<OrderItemAccount> accountList = orderItemAccountMapper.selectByOrderItem(orderItem.getId());
            List<BalanceChangeInfo> itemInfoList = AccountUtil.makerPayBalanceChangeInfo(accountList, orderItem.getPayment_flag(), orderItem.getSupplier_ep_id(), orderItem.getSupplier_core_ep_id(), order.getBuy_ep_id());
            balanceChangeInfoList.addAll(itemInfoList);
        }
        return balanceChangeInfoList;
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    @JobTask
    public void addPaymentCallback(Order order) {
        order.setStatus(OrderConstant.OrderStatus.PAYING); // 支付中
        // 支付成功回调 记录任务
        Map<String, String> jobParams = new HashMap<>();
        jobParams.put("orderId", order.getId().toString());
        jobParams.put("serialNum", "-1"); // 到付
        jobManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, Collections.singleton(jobParams));
        orderMapper.updateByPrimaryKeySelective(order);
    }
}
