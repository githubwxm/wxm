package com.all580.order.adapter;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.RefundVisitorMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.dto.RefundOrderApply;
import com.all580.order.entity.RefundVisitor;
import com.all580.order.entity.Visitor;
import com.framework.common.lang.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/8 14:25
 */
@Component(OrderConstant.REFUND_ADAPTER + OrderConstant.OrderAdapter.TICKET_OTA)
public class TicketRefundOrderOTAImpl extends TicketRefundOrderImpl {
    @Autowired
    private VisitorMapper visitorMapper;
    @Autowired
    private RefundVisitorMapper refundVisitorMapper;

    @Override
    public RefundOrderApply validateAndParseParams(long itemNo, Map params) {
        RefundOrderApply apply = super.validateAndParseParams(itemNo, params);
        if (!params.containsKey("days")) {
            List<Visitor> visitors = visitorMapper.selectByOrderItem(apply.getItem().getId());
            List<RefundVisitor> refundVisitors = refundVisitorMapper.selectByItemIdExcludeFailed(apply.getItem().getId());

            List<Map<String, Object>> days = new ArrayList<>();
            params.put("days", days);
            Map<String, Object> day = new HashMap<>();
            days.add(day);
            day.put("day", DateFormatUtils.parseDateToDatetimeString(apply.getItem().getStart()));
            List<Map<String, Integer>> vList = new ArrayList<>();
            day.put("visitors", vList);

            int quantity = getQuantity(apply, visitors, refundVisitors, vList);
            if (quantity <= 0) {
                throw new ApiException("没有可退的票");
            }

            day.put("quantity", quantity);
            apply.setQuantity(quantity);
        }
        return apply;
    }

    private int getQuantity(RefundOrderApply apply, List<Visitor> visitors, List<RefundVisitor> refundVisitors, List<Map<String, Integer>> vList) {
        if (apply.getQuantity() == null) {
            int dayQuantity = 0;
            for (Visitor visitor : visitors) {
                RefundVisitor refundVisitor = refundOrderManager.getRefundVisitorById(refundVisitors, visitor.getId());
                int quantity = visitor.getQuantity() - (refundVisitor != null ? refundVisitor.getPre_quantity() : 0) - visitor.getReturn_quantity() - visitor.getUse_quantity();
                if (quantity > 0) {
                    dayQuantity += quantity;
                    Map<String, Integer> map = new HashMap<>();
                    map.put("id", visitor.getId());
                    map.put("quantity", quantity);
                    vList.add(map);
                }
            }
            return dayQuantity;
        }
        if (visitors.size() > 1) {
            throw new ApiException("不止一个游客");
        }
        Visitor visitor = visitors.get(0);
        Map<String, Integer> map = new HashMap<>();
        map.put("id", visitor.getId());
        map.put("quantity", apply.getQuantity());
        vList.add(map);
        return apply.getQuantity();
    }
}
