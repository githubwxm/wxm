package com.all580.order.task.timer;

import com.all580.order.dao.OrderExceptionMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.manager.RefundOrderManager;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 取消订单定时器
 * @date 2016/10/13 16:26
 */
@Component
@Slf4j
public class ExceptionlOrderTimer {
    public static final Long interval=1000L*60*20;
    @Autowired
    private OrderExceptionMapper orderExceptionMapper;
    @Scheduled(fixedDelay =1000)
    public void exceptionOrderTimerJob() {
        Long start= new Date().getTime();
        List<Map<String,Object>> list = orderExceptionMapper.selectOrderSendException();

        if(!list.isEmpty()){
            orderExceptionMapper.insertOrderException(list);
        }
        Long end= new Date().getTime();
        Long operateTime =end-start;
        if(operateTime-interval>0){
           log.info("异常订单操作时间大于20分钟"+operateTime);
        }else{
            log.info("异常订单操作时间{}秒",(operateTime/1000));
            try {
                Thread.sleep(interval-operateTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
