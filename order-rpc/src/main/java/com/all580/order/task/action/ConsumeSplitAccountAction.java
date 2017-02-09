package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
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
 * @Description: 核销后分账任务
 * @date 2016/10/21 15:11
 */
@Component(OrderConstant.Actions.CONSUME_SPLIT_ACCOUNT)
@Slf4j
public class ConsumeSplitAccountAction implements JobRunner {
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
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
            log.warn("核销后分账任务,流水号为空");
            throw new Exception("流水号为空");
        }
        OrderClearanceSerial serial = orderClearanceSerialMapper.selectBySn(sn);
        if (serial == null) {
            log.warn("核销后分账任务,核销流水不存在");
            throw new Exception("核销流水不存在");
        }
        if (serial.getSplit() != null && serial.getSplit()) {
            return new Result(Action.EXECUTE_SUCCESS, "已经分账");
        }
        serial.setSplit(true);
        orderClearanceSerialMapper.updateByPrimaryKeySelective(serial);
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(serial.getOrder_item_id());
        bookingOrderManager.consumeOrReConsumeSplitAccount(orderItem, serial.getDay(), serial.getQuantity(), sn, true);

        // 同步数据
        bookingOrderManager.syncOrderAccountData(orderItem.getId());
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
