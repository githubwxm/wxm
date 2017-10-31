package com.all580.order.service.event;

import com.all580.order.api.OrderConstant;
import com.all580.order.api.service.event.PaidNotifyEvent;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import com.all580.product.api.consts.ProductConstants;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wxming on 2017/10/30 0030.
 */
@Service
@Slf4j
public class ReservePaidEventImpl implements PaidNotifyEvent {

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
        for(OrderItem item:list){
           if(item.getPro_type().intValue()- ProductConstants.ProductType.SCENERY==0){
               String key = OrderConstant.INDEX_DATA_CHANGE+item.getSupplier_ep_id();
               DistributedReentrantLock lock = distributedLockTemplate.execute(key, lockTimeOut);
               try{
                   Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                   ca.setTime(new Date()); //设置时间为当前时间
                   ca.add(Calendar.DATE, 1); //
                   Date lastMonth = ca.getTime(); //结果
                   String tomorrow= DateFormatUtils.converToStringDate(lastMonth);
                   String current =DateFormatUtils.converToStringDate(new Date());
                   String start=DateFormatUtils.converToStringDate(item.getStart());//游玩时间
                   String num="";
                   if(current.equals(start)){
                        num = redisUtils.get(key);
                       String [] nums= num.split(",");
                       Integer day = CommonUtil.objectParseInteger(nums[0])+item.getQuantity();
                       num=day+","+nums[1];
                       redisUtils.set(key,num);
                   }else if(tomorrow.equals(start)){
                       num = redisUtils.get(key);
                       String [] nums= num.split(",");
                       Integer day = CommonUtil.objectParseInteger(nums[1])+item.getQuantity();
                       num=nums[0]+","+day;
                       redisUtils.set(key,num);
                   }
               }catch (Exception e){
                 log.error("计算预计入园异常 itemId{} error{} ",item.getNumber(),e.getMessage());
               }finally {
                lock.unlock();
               }
           }
        }
        return new Result(true);
    }
}
