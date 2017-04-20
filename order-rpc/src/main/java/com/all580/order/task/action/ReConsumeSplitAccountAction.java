package com.all580.order.task.action;

import com.all580.order.api.OrderConstant;
import com.all580.order.dao.*;
import com.all580.order.dto.SyncAccess;
import com.all580.order.entity.ClearanceWashedSerial;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderClearanceSerial;
import com.all580.order.entity.OrderItem;
import com.all580.order.manager.BookingOrderManager;
import com.all580.order.service.event.BasicSyncDataEvent;
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
public class ReConsumeSplitAccountAction extends BasicSyncDataEvent implements JobRunner {
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;
    @Autowired
    private ClearanceWashedSerialMapper clearanceWashedSerialMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemAccountMapper orderItemAccountMapper;
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
        OrderClearanceSerial serial = orderClearanceSerialMapper.selectBySn(washedSerial.getClearance_serial_no());
        if (serial == null) {
            log.warn("反核销后分账任务,核销流水不存在");
            throw new Exception("核销流水不存在");
        }
        if (washedSerial.getSplit() != null && washedSerial.getSplit()) {
            return new Result(Action.EXECUTE_SUCCESS, "已经分账");
        }
        washedSerial.setSplit(true);
        clearanceWashedSerialMapper.updateByPrimaryKeySelective(washedSerial);
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(serial.getOrder_item_id());
        bookingOrderManager.consumeOrReConsumeSplitAccount(orderItem, washedSerial.getDay(), washedSerial.getQuantity(), sn, false);

        // 同步数据
        Order order = orderMapper.selectByPrimaryKey(orderItem.getOrder_id());
        SyncAccess syncAccess = getAccessKeys(order);
        syncAccess.getDataMap()
                .add("t_order_item_account", orderItemAccountMapper.selectByOrderItem(orderItem.getId()));
        syncAccess.loop();
        sync(syncAccess.getDataMaps());
        return new Result(Action.EXECUTE_SUCCESS);
    }
}
