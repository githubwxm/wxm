package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.ClearanceWashedSerialMapper;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.entity.ClearanceWashedSerial;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 反核销后分账任务
 * @date 2016/10/21 15:11
 */
@Component(OrderConstant.Actions.RE_CONSUME_SPLIT_ACCOUNT)
@Slf4j
public class ReConsumeSplitAccountAction implements JobRunner {
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private ClearanceWashedSerialMapper clearanceWashedSerialMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private BookingOrderManager bookingOrderManager;

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result run(JobContext jobContext) throws Throwable {
        Map<String, String> params = jobContext.getJob().getExtParams();
        String sn = params.get("sn");
        if (sn == null) {
            log.warn("反核销后分账任务,反流水号为空");
            throw new Exception("反流水号为空");
        }
        ClearanceWashedSerial washedSerial = clearanceWashedSerialMapper.selectBySn(sn);
        if (washedSerial == null) {
            log.warn("反核销后分账任务,反核销流水不存在");
            throw new Exception("反核销流水不存在");
        }
        OrderClearanceSerial serial = orderClearanceSerialMapper.selectBySn(washedSerial.getClearanceSerialNo());
        if (serial == null) {
            log.warn("反核销后分账任务,核销流水不存在");
            throw new Exception("核销流水不存在");
        }
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(serial.getOrderItemId());
        bookingOrderManager.consumeOrReConsumeSplitAccount(orderItem, washedSerial.getDay(), washedSerial.getQuantity(), sn, false);

        // 同步数据
        bookingOrderManager.syncReConsumeSplitAccountData(orderItem.getId());
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
