package com.all580.order.adapter;

import com.alibaba.fastjson.JSONObject;
import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.PackageOrderItemMapper;
import com.all580.order.dto.RefundDay;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.PackageOrderItem;
import com.all580.order.entity.RefundOrder;
import com.all580.order.manager.RefundOrderManager;
import com.all580.product.api.consts.ProductConstants;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.lang.exception.ApiException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangzw on 2017/7/14.
 */
@Component(OrderConstant.REFUND_ADAPTER + "PACKAGE")
public class PackageRefundOrderItemServiceImpl extends AbstractRefundOrderImpl{

    @Resource(name=OrderConstant.REFUND_ADAPTER + "TICKET",type = RefundOrderInterface.class)
    private RefundOrderInterface ticketRefundOrder;
    @Resource(name=OrderConstant.REFUND_ADAPTER + "HOTEL",type = RefundOrderInterface.class)
    private RefundOrderInterface hotelRefundOrder;
    @Resource(name=OrderConstant.REFUND_ADAPTER + "LINE",type = RefundOrderInterface.class)
    private RefundOrderInterface lineRefundOrder;

    @Autowired
    private PackageOrderItemMapper packageOrderItemMapper;

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
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        Integer productType = Integer.parseInt(String.valueOf(params.get("product_type")));

        return this.getCreateOrderInterface(productType).validateAndParseParams(itemNo, params);
    }

    @Override
    public void checkAuth(RefundOrderApply apply, Map params) {
        int epId = apply.getEpId();
        Order order = apply.getOrder();
        PackageOrderItem packageOrderItem = apply.getPackageOrderItem();
        if (apply.getFrom() == ProductConstants.RefundEqType.SELLER) {
            if (epId != order.getBuy_ep_id() && epId != order.getPayee_ep_id()) {
                throw new ApiException("非法请求:当前企业不能退订该订单");
            }
        } else {
            if (epId != packageOrderItem.getEp_id() && epId != order.getPayee_ep_id()) {
                throw new ApiException("非法请求:当前企业不能退订该订单");
            }
        }
    }

    @Override
    public void canBeRefund(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {
        if (apply.getItem() == null){
            try {
                this.getCreateOrderInterface(apply.getItem().getPro_type()).canBeRefund(apply, detailList, params);
            }catch (Exception e){
                //不可退元素的处理
            }
        }else {
            PackageOrderItem packageOrderItem = apply.getPackageOrderItem();
            String rule = apply.getFrom() == ProductConstants.RefundEqType.SELLER ? packageOrderItem.getCust_refund_rule() : packageOrderItem.getSaler_refund_rule();
            JSONObject jsonObject = JSONObject.parseObject(rule);
            Object tmp = jsonObject.get("refund");
            boolean refund = true;
            if (tmp != null) {
                String cs = tmp.toString();
                refund = StringUtils.isNumeric(cs) ? BooleanUtils.toBoolean(Integer.parseInt(cs)) : BooleanUtils.toBoolean(cs);
            }
            if (!refund) {
                throw new ApiException("该订单不可退");
            }
        }
    }

    @Override
    public Collection<RefundDay> getRefundDays(RefundOrderApply apply, List<OrderItemDetail> detailList, Map params) {

        return this.getCreateOrderInterface(apply.getItem().getPro_type()).getRefundDays(apply, detailList, params);
    }

    @Override
    public int getRefundQuantity(RefundOrderApply apply, Collection<RefundDay> refundDays, Map params) {

        return this.getCreateOrderInterface(apply.getItem().getPro_type()).getRefundQuantity(apply, refundDays, params);
    }

    @Override
    public void hasRemainAndInsertRefundVisitor(RefundOrderApply apply, RefundOrder refundOrder, List<OrderItemDetail> detailList, Collection<RefundDay> refundDays, Map params) {

        this.getCreateOrderInterface(apply.getItem().getPro_type()).hasRemainAndInsertRefundVisitor(apply, refundOrder, detailList, refundDays, params);
    }
}
