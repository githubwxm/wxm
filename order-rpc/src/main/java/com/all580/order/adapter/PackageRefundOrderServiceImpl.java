package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.RefundOrder;
import com.all580.order.entity.Visitor;
import com.all580.order.manager.RefundOrderManager;
import com.all580.product.api.consts.ProductConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/14.
 */
@Component(OrderConstant.REFUND_ADAPTER + "PACKAGE")
public class PackageRefundOrderServiceImpl extends AbstractRefundOrderImpl{

    @Resource(name=OrderConstant.REFUND_ADAPTER + "TICKET",type = RefundOrderInterface.class)
    private RefundOrderInterface ticketRefundOrder;
    @Resource(name=OrderConstant.REFUND_ADAPTER + "HOTEL",type = RefundOrderInterface.class)
    private RefundOrderInterface hotelRefundOrder;
    @Resource(name=OrderConstant.REFUND_ADAPTER + "LINE",type = RefundOrderInterface.class)
    private RefundOrderInterface lineRefundOrder;
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;

    @Override
    @Autowired
    public void setOrderItemMapper(OrderItemMapper orderItemMapper) {
        super.orderItemMapper = orderItemMapper;
    }

    @Override
    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        super.orderMapper = orderMapper;
    }

    @Override
    @Autowired
    public void setRefundOrderManager(RefundOrderManager refundOrderManager) {
        super.refundOrderManager = refundOrderManager;
    }

    private RefundOrderInterface getCreateOrderInterface(Integer productType){
        if(ProductConstants.ProductType.SCENERY == productType){
            return this.ticketRefundOrder;
        }
        if(ProductConstants.ProductType.HOTEL == productType){
            return this.hotelRefundOrder;
        }
        if(ProductConstants.ProductType.ITINERARY == productType){
            return this.lineRefundOrder;
        }
        throw new ApiException("未找到对应产品类型的订单接口");
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        //元素订单退订检查
        Integer productTYpe = apply.getItem().getPro_type();
        if (productTYpe == ProductConstants.ProductType.PACKAGE){
            super.canBeRefund(apply, detailList, params);
        }else {
            this.getCreateOrderInterface(productTYpe).canBeRefund(apply, detailList, params);
        }
    }

    @Override
    public Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        List<RefundDay> refundDays = new ArrayList<>();
        for (OrderItemDetail detail : detailList) {
            RefundDay refundDay = new RefundDay();
            refundDay.setDay(detail.getDay());
            refundDay.setQuantity(detail.getQuantity());
            if (apply.getItem().getPro_type() == ProductConstants.ProductType.SCENERY ||
                    apply.getItem().getPro_type() == ProductConstants.ProductType.ITINERARY ){
                List<Visitor> visitors = visitorMapper.selectByOrderItem(apply.getItem().getId());
                refundDay.setVisitors(visitors);
            }
            refundDays.add(refundDay);
        }
        return refundDays;
    }

    @Override
    public void validateRefundVisitor(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {

    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {
        Integer productTYpe = apply.getItem().getPro_type();
        if (productTYpe == ProductConstants.ProductType.PACKAGE){
            return apply.getItem().getQuantity();
        }
        return this.getCreateOrderInterface(productTYpe).getRefundQuantity(apply, refundDays, params);
    }

    @Override
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params) {
        Integer productTYpe = apply.getItem().getPro_type();
        if (productTYpe == ProductConstants.ProductType.PACKAGE){
            for (OrderItemDetail detail : detailList) {
                detail.setRefund_quantity(detail.getQuantity());
                orderItemDetailMapper.updateByPrimaryKeySelective(detail);
            }
        }else {
            this.getCreateOrderInterface(productTYpe).hasRemainAndInsertRefundVisitor(apply, refundOrder, detailList, refundDays, params);
        }
    }
}
