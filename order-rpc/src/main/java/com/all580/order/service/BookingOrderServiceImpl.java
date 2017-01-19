package com.all580.order.service;

import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.BookingOrderService;
import com.all580.order.dao.*;
import com.all580.order.dto.LockStockDto;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.manager.LockTransactionManager;
import com.all580.order.manager.RefundOrderManager;
import com.all580.order.manager.SmsManager;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.product.api.consts.ProductConstants;
import com.all580.product.api.model.EpSalesInfo;
import com.all580.product.api.model.ProductSalesDayInfo;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.model.ProductSearchParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.all580.voucher.api.model.ReSendTicketParams;
import com.all580.voucher.api.model.group.ModifyGroupTicketParams;
import com.all580.voucher.api.service.VoucherRPCService;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.util.TimeConsum;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import javax.lang.exception.ParamsMapValidationException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务实现
 * @date 2016/9/28 9:23
 */
@Service
@Slf4j
public class BookingOrderServiceImpl implements BookingOrderService {
    @Autowired
    private BookingOrderManager bookingOrderManager;
    @Autowired
    private RefundOrderManager refundOrderManager;
    @Autowired
    private SmsManager smsManager;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private MaSendResponseMapper maSendResponseMapper;
    @Autowired
    private RefundOrderMapper refundOrderMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;
    @Autowired
    private EpService epService;
    @Autowired
    private VoucherRPCService voucherRPCService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Autowired
    private LockTransactionManager lockTransactionManager;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> create(Map params) throws Exception {
        TimeConsum.MakerTime total = TimeConsum.maker();
        Integer buyEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID));
        // 获取平台商ID
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
        Integer from = CommonUtil.objectParseInteger(params.get("from"));
        String remark = CommonUtil.objectParseString(params.get("remark"));
        int totalSalePrice = 0;
        int totalPayPrice = 0;

        TimeConsum.maker();
        // 判断销售商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(buyEpId), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("销售商企业已冻结");
        }
        TimeConsum.how(true).print("判断销售商冻结");
        // 只有销售商可以下单
        Result<Integer> epType = epService.selectEpType(buyEpId);
        if (!bookingOrderManager.isEpType(epType, EpConstant.EpType.SELLER) &&
                !bookingOrderManager.isEpType(epType, EpConstant.EpType.OTA)) {
            throw new ApiException("该企业不能购买产品");
        }
        TimeConsum.how(true).print("判断企业类型");
        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();

        // 获取下单企业名称
        String buyEpName = null;
        Result<Map<String, Object>> epResult = epService.selectId(buyEpId);
        if (epResult != null && epResult.isSuccess() && epResult.get() != null) {
            buyEpName = String.valueOf(epResult.get().get("name"));
        }
        TimeConsum.how().print("获取下单企业名称");

        // 创建订单
        Order order = bookingOrderManager.generateOrder(coreEpId, buyEpId, buyEpName,
                CommonUtil.objectParseInteger(params.get("operator_id")),
                CommonUtil.objectParseString(params.get("operator_name")), from, remark, CommonUtil.objectParseString(params.get("outer_id")));

        // 存储游客信息
        Map<Integer, List<Visitor>> visitorMap = new HashMap<>();
        // 获取子订单
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items) {
            Long productSubCode = Long.parseLong(item.get("product_sub_code").toString());
            Integer quantity = CommonUtil.objectParseInteger(item.get("quantity"));
            Integer days = CommonUtil.objectParseInteger(item.get("days"));
            Integer allQuantity = quantity * days;
            int visitorQuantity = 0; // 游客总票数
            //预定日期
            Date bookingDate = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, CommonUtil.objectParseString(item.get("start")));

            TimeConsum.maker();
            // 验证是否可售
            ProductSearchParams searchParams = new ProductSearchParams();
            searchParams.setSubProductCode(productSubCode);
            searchParams.setStartDate(bookingDate);
            searchParams.setDays(days);
            searchParams.setQuantity(quantity);
            searchParams.setBuyEpId(buyEpId);
            Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
            if (!salesInfoResult.isSuccess()) {
                throw new ApiException(salesInfoResult.getError());
            }
            TimeConsum.how().print("验证是否可售");
            ProductSalesInfo salesInfo = salesInfoResult.get();
            // 判断供应商状态是否为已冻结
            if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEp_id()), EpConstant.EpStatus.ACTIVE)) {
                throw new ApiException("供应商企业已冻结");
            }

            List<ProductSalesDayInfo> dayInfoList = salesInfo.getDay_info_list();

            if (dayInfoList.size() != days) {
                throw new ApiException("预定天数与获取产品天数不匹配");
            }
            // 验证预定时间限制
            bookingOrderManager.validateBookingDate(bookingDate, dayInfoList);

            TimeConsum.maker();
            // 判断游客信息
            List<Map> visitors = (List<Map>) item.get("visitor");
            if (salesInfo.isRequire_sid()) {
                Result visitorResult = bookingOrderManager.validateVisitor(
                        visitors, salesInfo.getProduct_sub_code(), bookingDate, salesInfo.getSid_day_count(), salesInfo.getSid_day_quantity());
                if (!visitorResult.isSuccess()) {
                    throw new ApiException(visitorResult.getError());
                }
            }
            TimeConsum.how(true).print("验证游客信息");

            // 每天的价格
            List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();

            // 子订单总进货价
            int[] priceArray = bookingOrderManager.calcSalesPrice(allDaysSales, salesInfo, buyEpId, quantity, from);
            int salePrice = priceArray[0]; // 销售价
            totalSalePrice += salePrice;
            // 只计算预付的,到付不计算在内
            if (salesInfo.getPay_type() == ProductConstants.PayType.PREPAY) {
                totalPayPrice += salePrice;
            }
            TimeConsum.how(true).print("计算价格");

            // 创建子订单
            OrderItem orderItem = bookingOrderManager.generateItem(salesInfo, dayInfoList.get(dayInfoList.size() - 1).getEnd_time(), salePrice, bookingDate, days, order.getId(), quantity, null);

            List<OrderItemDetail> detailList = new ArrayList<>();
            List<Visitor> visitorList = new ArrayList<>();
            // 创建子订单详情
            int i = 0;
            for (ProductSalesDayInfo dayInfo : dayInfoList) {
                OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(dayInfo, orderItem.getId(), DateUtils.addDays(bookingDate, i), quantity, salesInfo.getLow_use_quantity());
                detailList.add(orderItemDetail);
                // 创建游客信息
                for (Map v : visitors) {
                    Visitor visitor = bookingOrderManager.generateVisitor(v, orderItemDetail.getId());
                    visitorQuantity += visitor.getQuantity();
                    visitorList.add(visitor);
                }
                i++;
            }
            TimeConsum.how().print("创建子订单、详情、游客");

            // 判断总张数是否匹配
            if (visitorQuantity != allQuantity) {
                throw new ApiException("游客票数与总票数不符");
            }
            // 判断最高票数 散客
            if (salesInfo.getMax_buy_quantity() != null && salesInfo.getMax_buy_quantity() > 0 && visitorQuantity > salesInfo.getMax_buy_quantity()) {
                throw new ApiException(String.format("超过订单最高购买限制: 当前购买:%d, 最大购买:%d", visitorQuantity, salesInfo.getMax_buy_quantity()));
            }
            lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, detailList, dayInfoList));
            lockParams.add(bookingOrderManager.parseParams(orderItem));
            visitorMap.put(orderItem.getId(), visitorList);

            TimeConsum.maker();
            // 预分账记录
            bookingOrderManager.prePaySplitAccount(allDaysSales, orderItem, buyEpId);
            TimeConsum.how().print("预分账");
        }

        // 创建订单联系人
        Map shippingMap = (Map) params.get("shipping");
        bookingOrderManager.generateShipping(shippingMap, order.getId());

        TimeConsum.maker();
        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getError());
        }
        TimeConsum.how().print("锁定库存");

        Map<Integer, List<Boolean>> listMap = lockResult.get();
        if (listMap.size() != lockStockDtoMap.size()) {
            throw new ApiException("锁定库存异常");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        // 检查是否待审核
        checkCreateAudit(lockStockDtoMap, listMap, orderItems);

        // 更新订单状态
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                break;
            }
        }

        // 更新订单金额
        order.setPay_amount(totalPayPrice);
        order.setSale_amount(totalSalePrice);

        // 更新审核时间
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getAudit_time() == null) {
            order.setAudit_time(new Date());
        }

        // 到付
        addPaymentCallbackJob(order);
        orderMapper.updateByPrimaryKeySelective(order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", JsonUtils.obj2map(order));
        resultMap.put("items", JsonUtils.json2List(JsonUtils.toJson(orderItems)));
        resultMap.put("visitors", JsonUtils.obj2map(visitorMap));
        Result<Object> result = new Result<>(true);
        result.put(resultMap);

        TimeConsum.maker();
        // 同步数据
        Map syncData = bookingOrderManager.syncCreateOrderData(order.getId());
        result.putExt(Result.SYNC_DATA, syncData);
        TimeConsum.how().print("同步数据");
        total.how().print(order.getNumber() + " 下单总");
        return result;
    }

    @Override
    public Result<?> audit(Map params) {
        String orderItemId = params.get("order_item_id").toString();
        long orderItemSn = Long.valueOf(orderItemId);
        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderItemId, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.auditBooking(params, orderItemSn);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> payment(Map params) {
        String orderSn = params.get("order_sn").toString();
        Long sn = Long.valueOf(orderSn);
        Integer payType = CommonUtil.objectParseInteger(params.get("pay_type"));

        // 分布式锁
        DistributedReentrantLock lock = distributedLockTemplate.execute(orderSn, lockTimeOut);

        // 锁成功
        try {
            return lockTransactionManager.payment(params, sn, payType);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Result<?> resendTicket(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.TICKET_FAIL,
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND
        }, orderItem.getStatus()) < 0) {
            throw new ApiException("子订单不在可重新发票状态");
        }

        // 是否全部重新发票
        Boolean all = Boolean.parseBoolean(CommonUtil.objectParseString(params.get("all")));
        if (all) {
            List<Visitor> visitors = visitorMapper.selectByOrderItem(orderItem.getId());
            for (Visitor visitor : visitors) {
                reSendTicket(orderItem, visitor.getId(), visitor.getPhone());
            }
        } else {
            Integer visitorId = CommonUtil.objectParseInteger(params.get("visitor_id"));
            if (visitorId == null) {
                throw new ParamsMapValidationException("visitor_id", "游客ID不能为空");
            }
            Result result = reSendTicket(orderItem, visitorId, CommonUtil.objectParseString(params.get("phone")));
            if (!result.isSuccess()) {
                throw new ApiException(result.getError());
            }
        }
        return new Result<>(true);
    }

    @Override
    public Result<?> resendTicketForGroup(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            throw new ApiException("子订单不存在");
        }
        if (ArrayUtils.indexOf(new int[]{
                OrderConstant.OrderItemStatus.TICKET_FAIL,
                OrderConstant.OrderItemStatus.SEND,
                OrderConstant.OrderItemStatus.NON_SEND
        }, orderItem.getStatus()) < 0) {
            throw new ApiException("子订单不在可重新发票状态");
        }

        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            return new Result(false, "该订单不是团队订单");
        }

        List<RefundOrder> refundOrderList = refundOrderMapper.selectByItemId(orderItem.getId());
        if (refundOrderList != null && refundOrderList.size() > 0) {
            throw new ApiException("该子订单已发起退票");
        }

        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND) {
            return voucherRPCService.resendGroupTicket(orderItem.getEp_ma_id(), orderItem.getNumber());
        }
        // 出票
        // 记录任务
        Map<String, String> jobMap = new HashMap<>();
        jobMap.put("orderItemId", orderItem.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.SEND_GROUP_TICKET, jobMap);
        return new Result<>(true);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result<?> createForGroup(Map params) throws Exception {
        Integer buyEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.EP_ID));
        // 获取平台商ID
        Integer coreEpId = CommonUtil.objectParseInteger(params.get(EpConstant.EpKey.CORE_EP_ID));
        Integer from = CommonUtil.objectParseInteger(params.get("from"));
        String remark = CommonUtil.objectParseString(params.get("remark"));
        Integer groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        int totalSalePrice = 0;
        int totalPayPrice = 0;

        // 验证团队
        Group group = groupMapper.selectByPrimaryKey(groupId);
        if (group == null) {
            throw new ApiException("团队不存在");
        }
        if (group.getCore_ep_id().intValue() != coreEpId) {
            throw new ApiException("团队不属于本平台");
        }

        // 判断销售商状态是否为已冻结
        if (!bookingOrderManager.isEpStatus(epService.getEpStatus(buyEpId), EpConstant.EpStatus.ACTIVE)) {
            throw new ApiException("销售商企业已冻结");
        }
        // 只有销售商可以下单
        Result<Integer> epType = epService.selectEpType(buyEpId);
        if (!bookingOrderManager.isEpType(epType, EpConstant.EpType.SELLER) &&
                !bookingOrderManager.isEpType(epType, EpConstant.EpType.OTA)) {
            throw new ApiException("该企业不能购买产品");
        }

        // 锁定库存集合(统一锁定)
        Map<Integer, LockStockDto> lockStockDtoMap = new HashMap<>();
        List<ProductSearchParams> lockParams = new ArrayList<>();

        // 获取下单企业名称
        String buyEpName = null;
        Result<Map<String, Object>> epResult = epService.selectId(buyEpId);
        if (epResult != null && epResult.isSuccess() && epResult.get() != null) {
            buyEpName = String.valueOf(epResult.get().get("name"));
        }

        // 创建订单
        Order order = bookingOrderManager.generateOrder(coreEpId, buyEpId, buyEpName,
                CommonUtil.objectParseInteger(params.get("operator_id")),
                CommonUtil.objectParseString(params.get("operator_name")), from, remark, CommonUtil.objectParseString(params.get("outer_id")));

        // 存储游客信息
        Map<Integer, List<Visitor>> visitorMap = new HashMap<>();
        // 获取子订单
        List<Map> items = (List<Map>) params.get("items");
        for (Map item : items) {
            Long productSubCode = Long.parseLong(item.get("product_sub_code").toString());
            boolean special = BooleanUtils.toBoolean(CommonUtil.objectParseString(item.get("special")));
            Integer quantity = CommonUtil.objectParseInteger(item.get("quantity"));
            Integer days = CommonUtil.objectParseInteger(item.get("days"));
            Integer allQuantity = quantity * days;
            //预定日期
            Date bookingDate = DateFormatUtils.parseString(DateFormatUtils.DATE_TIME_FORMAT, CommonUtil.objectParseString(item.get("start")));
            // 验证出游日期
            if (bookingDate.before(group.getStart_date())) {
                throw new ApiException("预定日期不能小于团队出游日期");
            }

            // 验证是否可售
            ProductSearchParams searchParams = new ProductSearchParams();
            searchParams.setSubProductCode(productSubCode);
            searchParams.setStartDate(bookingDate);
            searchParams.setDays(days);
            searchParams.setQuantity(quantity);
            searchParams.setBuyEpId(buyEpId);
            Result<ProductSalesInfo> salesInfoResult = productSalesPlanRPCService.validateProductSalesInfo(searchParams);
            if (!salesInfoResult.isSuccess()) {
                throw new ApiException(salesInfoResult.getError());
            }
            ProductSalesInfo salesInfo = salesInfoResult.get();
            // 判断供应商状态是否为已冻结
            if (!bookingOrderManager.isEpStatus(epService.getEpStatus(salesInfo.getEp_id()), EpConstant.EpStatus.ACTIVE)) {
                throw new ApiException("供应商企业已冻结");
            }

            List<ProductSalesDayInfo> dayInfoList = salesInfo.getDay_info_list();

            if (dayInfoList.size() != days) {
                throw new ApiException("预定天数与获取产品天数不匹配");
            }

            // 验证预定时间限制
            bookingOrderManager.validateBookingDate(bookingDate, dayInfoList);

            // 验证最低购票
            if (salesInfo.getProduct_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM && salesInfo.getMin_buy_quantity() != null && salesInfo.getMin_buy_quantity() > allQuantity) {
                throw new ApiException("低于最低购买票数");
            }
            // 判断最高票数 散客
            if (salesInfo.getProduct_sub_ticket_type() == ProductConstants.TeamTicketType.INDIVIDUAL &&
                    salesInfo.getMax_buy_quantity() != null && salesInfo.getMax_buy_quantity() > 0 && allQuantity > salesInfo.getMax_buy_quantity()) {
                throw new ApiException(String.format("超过订单最高购买限制: 当前购买:%d, 最大购买:%d", allQuantity, salesInfo.getMax_buy_quantity()));
            }

            // 实名制验证
            List<GroupMember> groupMemberList = null;
            List visitors = (List) item.get("visitor");
            if (special) {
                // 团队买散客票 特殊处理
                if (salesInfo.isRequire_sid()) {
                    Map<String[], ValidRule[]> rules = new HashMap<>();
                    // 校验不为空的参数
                    rules.put(new String[]{
                            "visitor.name", // 订单游客姓名
                            "visitor.phone", // 订单游客手机号码
                            "visitor.sid", // 订单游客身份证号码
                            "visitor.quantity" // 张数
                    }, new ValidRule[]{new ValidRule.NotNull()});
                    rules.put(new String[]{"visitor.sid"}, new ValidRule[]{new ValidRule.IdCard()});
                    rules.put(new String[]{"visitor.quantity"}, new ValidRule[]{new ValidRule.Digits()});
                    rules.put(new String[]{"visitor.phone"}, new ValidRule[]{new ValidRule.Pattern(ValidRule.MOBILE_PHONE)});
                    ParamsMapValidate.validate(Collections.singletonMap("visitor", visitors), rules);

                    Result visitorResult = bookingOrderManager.validateVisitor(
                            visitors, salesInfo.getProduct_sub_code(), bookingDate, salesInfo.getSid_day_count(), salesInfo.getSid_day_quantity());
                    if (!visitorResult.isSuccess()) {
                        throw new ApiException(visitorResult.getError());
                    }
                }
            } else {
                Result<List<GroupMember>> validateResult = bookingOrderManager.validateGroupVisitor(visitors, salesInfo.getReal_name(), quantity, groupId);
                if (!validateResult.isSuccess()) {
                    throw new ApiException(validateResult.getError());
                }
                groupMemberList = validateResult.get();
            }

            // 每天的价格
            List<List<EpSalesInfo>> allDaysSales = salesInfo.getSales();

            // 计算分销价格
            int[] priceArray = bookingOrderManager.calcSalesPrice(allDaysSales, salesInfo, buyEpId, quantity, from);
            int salePrice = priceArray[0]; // 销售价
            totalSalePrice += salePrice;
            // 只计算预付的,到付不计算在内
            if (salesInfo.getPay_type() == ProductConstants.PayType.PREPAY) {
                totalPayPrice += salePrice;
            }

            // 创建子订单
            OrderItem orderItem = bookingOrderManager.generateItem(salesInfo, dayInfoList.get(dayInfoList.size() - 1).getEnd_time(), salePrice, bookingDate, days, order.getId(), quantity, groupId);

            List<OrderItemDetail> detailList = new ArrayList<>();
            List<Visitor> visitorList = new ArrayList<>();
            // 创建子订单详情
            int i = 0;
            int visitorQuantity = 0;
            for (ProductSalesDayInfo dayInfo : dayInfoList) {
                OrderItemDetail orderItemDetail = bookingOrderManager.generateDetail(dayInfo, orderItem.getId(), DateUtils.addDays(bookingDate, i), quantity, salesInfo.getLow_use_quantity());
                detailList.add(orderItemDetail);
                // 创建游客信息
                if (!special) {
                    if (groupMemberList != null) {
                        for (GroupMember member : groupMemberList) {
                            visitorList.add(bookingOrderManager.generateGroupVisitor(member, orderItemDetail.getId(), groupId));
                        }
                    }
                } else {
                    for (Object v : visitors) {
                        Visitor visitor = bookingOrderManager.generateVisitor((Map) v, orderItemDetail.getId());
                        visitorQuantity += visitor.getQuantity();
                        visitorList.add(visitor);
                    }
                }
                i++;
            }

            // 判断总张数是否匹配
            if (special && visitorQuantity != allQuantity) {
                throw new ApiException("游客票数与总票数不符");
            }

            lockStockDtoMap.put(orderItem.getId(), new LockStockDto(orderItem, detailList, dayInfoList));
            lockParams.add(bookingOrderManager.parseParams(orderItem));
            visitorMap.put(orderItem.getId(), visitorList);

            // 预分账记录
            bookingOrderManager.prePaySplitAccount(allDaysSales, orderItem, order.getBuy_ep_id());
        }
        // 创建订单联系人
        Shipping shipping = new Shipping();
        shipping.setOrder_id(order.getId());
        shipping.setName(group.getGuide_name());
        shipping.setPhone(group.getGuide_phone());
        shipping.setSid(group.getGuide_sid());
        shippingMapper.insertSelective(shipping);

        // 锁定库存
        Result<Map<Integer, List<Boolean>>> lockResult = productSalesPlanRPCService.lockProductStocks(lockParams);
        if (!lockResult.isSuccess()) {
            throw new ApiException(lockResult.getError());
        }

        Map<Integer, List<Boolean>> listMap = lockResult.get();
        if (listMap.size() != lockStockDtoMap.size()) {
            throw new ApiException("锁定库存异常");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        // 检查是否待审核
        checkCreateAudit(lockStockDtoMap, listMap, orderItems);

        // 更新订单状态
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getStatus() == OrderConstant.OrderItemStatus.AUDIT_WAIT) {
                order.setStatus(OrderConstant.OrderStatus.AUDIT_WAIT);
                break;
            }
        }

        // 更新订单金额
        order.setPay_amount(totalPayPrice);
        order.setSale_amount(totalSalePrice);

        // 更新审核时间
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getAudit_time() == null) {
            order.setAudit_time(new Date());
        }

        // 到付
        addPaymentCallbackJob(order);
        orderMapper.updateByPrimaryKeySelective(order);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", JsonUtils.obj2map(order));
        resultMap.put("items", JsonUtils.json2List(JsonUtils.toJson(orderItems)));
        resultMap.put("visitors", JsonUtils.obj2map(visitorMap));
        Result<Object> result = new Result<>(true);
        result.put(resultMap);

        // 同步数据
        Map syncData = bookingOrderManager.syncCreateOrderData(order.getId());
        result.putExt(Result.SYNC_DATA, syncData);
        return result;
    }

    @Override
    public Result<?> modifyTicketForGroup(Map params) {
        OrderItem orderItem = orderItemMapper.selectBySN(Long.valueOf(params.get("order_item_sn").toString()));
        if (orderItem == null) {
            return new Result(false, "订单不存在");
        }
        if (!(orderItem.getGroup_id() != null && orderItem.getGroup_id() != 0 &&
                orderItem.getPro_sub_ticket_type() != null && orderItem.getPro_sub_ticket_type() == ProductConstants.TeamTicketType.TEAM)) {
            return new Result(false, "该订单不是团队订单");
        }
        if ((orderItem.getStatus() != OrderConstant.OrderItemStatus.SEND &&
                orderItem.getStatus() != OrderConstant.OrderItemStatus.MODIFY &&
                orderItem.getStatus() != OrderConstant.OrderItemStatus.MODIFY_FAIL) ||
                (orderItem.getUsed_quantity() != null && orderItem.getUsed_quantity() > 0)) {
            return new Result(false, "该订单不在可修改状态");
        }

        ModifyGroupTicketParams ticketParams = new ModifyGroupTicketParams();
        ticketParams.setOrderSn(orderItem.getNumber());
        String guideName = CommonUtil.objectParseString(params.get("guide_name"));
        if (StringUtils.isNotEmpty(guideName)) {
            ticketParams.setGuideName(guideName);
        }
        String guidePhone = CommonUtil.objectParseString(params.get("guide_phone"));
        if (StringUtils.isNotEmpty(guidePhone)) {
            ticketParams.setGuidePhone(guidePhone);
        }
        String guideSid = CommonUtil.objectParseString(params.get("guide_sid"));
        if (StringUtils.isNotEmpty(guideSid)) {
            ticketParams.setGuideSid(guideSid);
        }
        String guideCard = CommonUtil.objectParseString(params.get("guide_card"));
        if (StringUtils.isNotEmpty(guideCard)) {
            ticketParams.setGuideCard(guideCard);
        }
        List visitors = (List) params.get("visitors");
        if (visitors != null && visitors.size() > 0) {
            List<com.all580.voucher.api.model.Visitor> vs = new ArrayList<>();
            for (Object o : visitors) {
                Map visitor = (Map) o;
                String name = CommonUtil.objectParseString(visitor.get("name"));
                if (StringUtils.isNotEmpty(name)) {
                    com.all580.voucher.api.model.Visitor v = new com.all580.voucher.api.model.Visitor();
                    v.setName(name);
                    v.setPhone(visitor.get("phone").toString());
                    v.setSid(visitor.get("sid").toString());
                    vs.add(v);
                }
            }
            ticketParams.setVisitors(vs);
        }
        return voucherRPCService.modifyGroupTicket(orderItem.getEp_ma_id(), ticketParams);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void checkCreateAudit(Map<Integer, LockStockDto> lockStockDtoMap, Map<Integer, List<Boolean>> listMap, List<OrderItem> orderItems) {
        for (Integer itemId : listMap.keySet()) {
            int i = 0;
            // 判断是否需要审核
            LockStockDto lockStockDto = lockStockDtoMap.get(itemId);
            OrderItem item = lockStockDto.getOrderItem();
            List<Boolean> booleanList = listMap.get(itemId);
            List<OrderItemDetail> orderItemDetail = lockStockDto.getOrderItemDetail();
            List<ProductSalesDayInfo> dayInfoList = lockStockDto.getDayInfoList();
            if (booleanList.size() != orderItemDetail.size() || booleanList.size() != dayInfoList.size()) {
                throw new ApiException(String.format("锁库存天数:%d与购买天数:%d不匹配", booleanList.size(), orderItemDetail.size()));
            }
            for (Boolean oversell : booleanList) {
                OrderItemDetail detail = orderItemDetail.get(i);
                ProductSalesDayInfo dayInfo = dayInfoList.get(i);
                detail.setOversell(oversell);
                // 如果是超卖则把子订单状态修改为待审
                if (oversell &&
                        (dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.OVERSELL_NEED_AUDIT ||
                                dayInfo.getSale_rule_type() == ProductConstants.SalesRuleType.ANYWAY_NEED_AUDIT) &&
                        item.getStatus() == OrderConstant.OrderItemStatus.AUDIT_SUCCESS) {
                    item.setStatus(OrderConstant.OrderItemStatus.AUDIT_WAIT);
                    orderItemMapper.updateByPrimaryKeySelective(item);
                    smsManager.sendAuditSms(item);
                }
                // 更新子订单详情
                orderItemDetailMapper.updateByPrimaryKeySelective(detail);
                i++;
            }
            orderItems.add(item);
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void addPaymentCallbackJob(Order order) {
        if (order.getStatus() != OrderConstant.OrderStatus.AUDIT_WAIT && order.getPay_amount() <= 0) {
            order.setStatus(OrderConstant.OrderStatus.PAYING); // 支付中
            // 支付成功回调 记录任务
            Map<String, String> jobParams = new HashMap<>();
            jobParams.put("orderId", order.getId().toString());
            jobParams.put("serialNum", "-1"); // 到付
            bookingOrderManager.addJob(OrderConstant.Actions.PAYMENT_CALLBACK, jobParams);
        }
    }

    private Result reSendTicket(OrderItem orderItem, Integer visitorId, String phone) {
        RefundOrder refundOrder = refundOrderMapper.selectByItemIdAndVisitor(orderItem.getId(), visitorId);
        if (refundOrder != null) {
            return new Result(false, "该子订单已发起退票");
        }
        MaSendResponse response = maSendResponseMapper.selectByVisitorId(orderItem.getId(), visitorId, orderItem.getEp_ma_id());
        if (orderItem.getStatus() == OrderConstant.OrderItemStatus.SEND && response != null) {
            ReSendTicketParams reSendTicketParams = new ReSendTicketParams();
            reSendTicketParams.setOrderSn(orderItem.getNumber());
            reSendTicketParams.setVisitorId(visitorId);
            reSendTicketParams.setMobile(phone);
            return voucherRPCService.resendTicket(orderItem.getEp_ma_id(), reSendTicketParams);
        }
        // 出票
        // 记录任务
        Map<String, String> jobParam = new HashMap<>();
        jobParam.put("orderItemId", orderItem.getId().toString());
        bookingOrderManager.addJob(OrderConstant.Actions.SEND_TICKET, jobParam);
        return new Result(true);
    }
}
