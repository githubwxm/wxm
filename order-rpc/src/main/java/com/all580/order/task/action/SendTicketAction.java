package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderItemDetailMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.dao.VisitorMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.order.entity.OrderItemDetail;
import com.all580.order.entity.Visitor;
import com.all580.product.api.model.Contact;
import com.all580.product.api.model.GetTicketParams;
import com.all580.product.api.service.ProductSalesPlanRPCService;
import com.framework.common.validate.ParamsMapValidate;
import com.framework.common.validate.ValidRule;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票任务
 * @date 2016/10/25 11:30
 */
@Component(OrderConstant.Actions.SEND_TICKET)
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class SendTicketAction implements JobRunner {
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemDetailMapper orderItemDetailMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    @Autowired
    private ProductSalesPlanRPCService productSalesPlanRPCService;

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        validateParams(params);

        int orderId = Integer.parseInt(params.get("orderId"));
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            log.warn("出票任务,订单不存在");
            throw new Exception("订单不存在");
        }

        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            List<OrderItemDetail> detailList = orderItemDetailMapper.selectByItemId(orderItem.getId());
            OrderItemDetail detail = detailList.get(0); // 景点只有一天
            GetTicketParams getTicketParams = new GetTicketParams();
            getTicketParams.setSn(orderItem.getNumber());
            getTicketParams.setBookinDate(orderItem.getStart());
            getTicketParams.setCreateTime(order.getCreateTime());
            getTicketParams.setProductSubId(orderItem.getProSubId());
            getTicketParams.setDisableDate(detail.getDisableDay());
            getTicketParams.setDisableWeek(detail.getDisableWeek());
            getTicketParams.setValidStart(detail.getEffectiveDate());
            getTicketParams.setValidEnd(detail.getExpiryDate());
            List<Visitor> visitorList = visitorMapper.selectByOrderDetailId(detail.getId());
            List<Contact> contacts = new ArrayList<>();
            for (Visitor visitor : visitorList) {
                Contact contact = new Contact();
                contact.setName(visitor.getName());
                contact.setPhone(visitor.getPhone());
                contact.setSid(visitor.getSid());
                contact.setQuantity(visitor.getQuantity());
                contacts.add(contact);
            }
            getTicketParams.setVisitors(contacts);
            com.framework.common.Result r = productSalesPlanRPCService.invokeVoucherFotTicket(getTicketParams);
            if (r.hasError()) {
                log.warn("子订单:{},出票失败:{}", orderItem.getNumber(), r.getError());
            }
        }
        return new Result(Action.EXECUTE_SUCCESS);
    }

    /**
     * 验证参数
     * @param params
     */
    private void validateParams(Map<String, String> params) {
        if (params == null) {
            throw new RuntimeException("出票任务参数为空");
        }
        Map<String[], ValidRule[]> rules = new HashMap<>();
        rules.put(new String[]{
                "orderId" // 订单ID
        }, new ValidRule[]{new ValidRule.NotNull(), new ValidRule.Digits()});

        ParamsMapValidate.validate(params, rules);
    }
}
