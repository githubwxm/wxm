package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.GroupMapper;
import com.all580.order.dao.ShippingMapper;
import com.all580.order.dto.ValidateProductSub;
import com.all580.order.entity.*;
import com.all580.product.api.model.ProductSalesInfo;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description: 酒店团队订单创建适配器
 * @date 17-5-9 上午9:49
 */
@Component(OrderConstant.CREATE_ADAPTER + "HOTEL_GROUP")
public class HotelGroupCreateOrderImpl extends TicketGroupCreateOrderImpl {
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public void validateVisitor(ProductSalesInfo salesInfo, ValidateProductSub sub, List<?> visitorList, Map item) {

    }

    @Override
    public List<Visitor> insertVisitor(List<?> visitorList, OrderItem orderItem, ProductSalesInfo salesInfo, ValidateProductSub sub, Map item) {
        return null;
    }

    @Override
    public Shipping insertShipping(Map params, Order order) {
        Integer groupId = CommonUtil.objectParseInteger(((List<Map>) params.get("items")).get(0).get("group_id"));
        String phone = CommonUtil.objectParseString(((List<Map>) params.get("items")).get(0).get("phone"));
        Group group = groupMapper.selectByPrimaryKey(groupId);
        Shipping shipping = new Shipping();
        shipping.setOrder_id(order.getId());
        shipping.setName(group.getGuide_name());
        shipping.setPhone(StringUtils.isEmpty(phone) ? group.getGuide_phone() : phone);
        shipping.setSid(group.getGuide_sid());
        shippingMapper.insertSelective(shipping);
        return shipping;
    }
}
