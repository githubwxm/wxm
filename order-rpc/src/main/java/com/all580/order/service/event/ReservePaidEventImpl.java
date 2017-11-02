package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.ReservePaidEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by wxming on 2017/10/30 0030.
 */
@Service
@Slf4j
public class ReservePaidEventImpl implements ReservePaidEvent {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 1000;
    @Override
    public Result process(String s, Integer integer, Date date) {
        Order order = orderMapper.selectByPrimaryKey(integer);
        Assert.notNull(order, "订单不存在");
        List<OrderItem> list = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : list) {
            if (item.getPro_type() == ProductConstants.ProductType.SCENERY) {
                String dateStr = DateFormatUtils.DATE_FORMAT.format(item.getStart());
                String key = String.format("%s%d:%s", OrderConstant.INDEX_DATA_CHANGE, item.getSupplier_ep_id(), dateStr);
                boolean exists = redisUtils.exists(key);
                redisUtils.incrBy(key, item.getQuantity());
                if (!exists) {
                    Date expireDate = DateUtils.addDays(item.getStart(), 3);
                    redisUtils.expireAt(key, expireDate.getTime() / 1000);
                }
            }
        }
        return new Result(true);
    }
}
