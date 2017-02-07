package com.all580.order.adapter;

import com.all580.ep.api.service.EpService;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.GroupMapper;
import com.all580.order.dao.ShippingMapper;
import com.all580.order.dto.CreateOrder;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.order.manager.BookingOrderManager;
import com.all580.product.api.model.ProductSalesInfo;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 门票团队创建订单适配器
 * @date 2017/2/7 11:24
 */
@Component(OrderConstant.CREATE_ADAPTER + "TICKET_GROUP")
public class TicketGroupCreateOrderImpl extends AbstractCreateOrderImpl {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ProductSalesInfo validateProductAndGetSales(ValidateProductSub sub, CreateOrder createOrder, Map item) {
        Group group = groupMapper.selectByPrimaryKey(sub.getGroupId());
        Assert.notNull(group, "团队不存在");
        if (group.getCore_ep_id() != createOrder.getCoreEpId()) {
            throw new ApiException("团队不属于本平台");
        }
        // 验证出游日期
        if (sub.getBooking().before(group.getStart_date())) {
            throw new ApiException("预定日期不能小于团队出游日期");
        }
        return super.validateProductAndGetSales(sub, createOrder, item);
    }

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {
        boolean special = BooleanUtils.toBoolean(CommonUtil.objectParseString(item.get("special")));
        if (special) {
            Assert.notEmpty(visitorList);
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
                ParamsMapValidate.validate(Collections.singletonMap("visitor", visitorList), rules);

                int total = bookingOrderManager.validateVisitor(
                        (List<Map>) visitorList, salesInfo.getProduct_sub_code(), sub.getBooking(), salesInfo.getSid_day_count(), salesInfo.getSid_day_quantity());
                if (total != sub.getAllQuantity()) {
                    throw new ApiException("游客票数与总票数不符");
                }
            }
        }
    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, List<OrderItemDetail> details, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        boolean special = BooleanUtils.toBoolean(CommonUtil.objectParseString(item.get("special")));
        if (special) {
            return super.insertVisitor(visitorList, details, salesInfo, sub, item);
        }
        Result<List<GroupMember>> validateResult = bookingOrderManager.validateGroupVisitor(visitorList, salesInfo.getReal_name(), sub.getQuantity(), sub.getGroupId());
        if (!validateResult.isSuccess()) {
            throw new ApiException(validateResult.getError());
        }
        List<GroupMember> members = validateResult.get();

        List<Visitor> visitors = new ArrayList<>();
        for (OrderItemDetail detail : details) {
            for (GroupMember member : members) {
                visitors.add(bookingOrderManager.generateGroupVisitor(member, detail.getId(), sub.getGroupId()));
            }
        }
        return visitors;
    }

    @Override
    public Shipping insertShipping(Map params, Order order) {
        Integer groupId = CommonUtil.objectParseInteger(params.get("group_id"));
        Group group = groupMapper.selectByPrimaryKey(groupId);
        Shipping shipping = new Shipping();
        shipping.setOrder_id(order.getId());
        shipping.setName(group.getGuide_name());
        shipping.setPhone(group.getGuide_phone());
        shipping.setSid(group.getGuide_sid());
        shippingMapper.insertSelective(shipping);
        return shipping;
    }

    @Override
    @Autowired
    public void setBookingOrderManager(BookingOrderManager bookingOrderManager) {
        super.bookingOrderManager = bookingOrderManager;
    }

    @Override
    @Autowired
    public void setEpService(EpService epService) {
        super.epService = epService;
    }

    @Override
    @Autowired
    public void setProductSalesPlanRPCService(ProductSalesPlanRPCService productSalesPlanRPCService) {
        super.productSalesPlanRPCService = productSalesPlanRPCService;
    }
}
