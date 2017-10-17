package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.framework.common.Result;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.lang.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Linv2 on 2017-05-27.
 */
@Component
@Slf4j
public class ConsumeOrderManager {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    public Integer consume(String seqId,
                            Integer number,
                            String address,
                            Date consumeTime,
                            String deviceId,
                            Order order) throws ApiException {


        if (StringUtils.isEmpty(seqId)) {
            throw new ApiException("无效的验证流水号");
        }
        if (number == null || number < 1) {
            throw new ApiException("无效的验证数量");
        }
        Consume consume = consumeMapper.selectBySeqId(number, null, order.getSupply_id(), seqId);
        if (consume != null) {
            throw new ApiException(Result.UNIQUE_KEY_ERROR, "重复的验证操作请求");
        }
        Integer availableNumber = order.getNumber() -
                (order.getConsume() == null ? 0 : order.getConsume())
                + (order.getReverse() == null ? 0 : order.getReverse())
                - (order.getRefunding() == null ? 0 : order.getRefunding())
                - (order.getRefund() == null ? 0 : order.getRefund());
        if (number > availableNumber) {
            throw new ApiException("消费数量大于订单剩余数量");
        }
        Date validTime = order.getValidTime();
        if (validTime != null && validTime.getTime() > System.currentTimeMillis()) {
            throw new ApiException("订单还未生效,生效开始时间：[" + DateFormatUtils.converToStringTime(validTime)+ "]");
        }
        Date invalidTime = order.getInvalidTime();
        if (invalidTime != null && invalidTime.getTime() < System.currentTimeMillis()) {
            throw new ApiException("订单已失效,失效时间：[" + DateFormatUtils.converToStringTime(invalidTime)+ "]");
        }
        String validWeek = order.getValidWeek();
        if (!StringUtils.isEmpty(validWeek) && validWeek.length() == 7) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(consumeTime);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 2;
            week = week < 0 ? 6 : week;
            if (validWeek.toCharArray()[week] == '0') {
                throw new ApiException("该订单星期" + (week + 1) + "不可用");
            }
        }
        String invalidDate = order.getInvalidDate();
        if (!StringUtils.isEmpty(invalidDate)) {
            String[] invalidArr = invalidDate.split(",");
            String consumeDay = DateFormatUtils.parseDate(DateFormatUtils.DATE_FORMAT, consumeTime);
            for (String invalidDay : invalidArr) {
                if (invalidDay.equals(consumeDay)) {
                    throw new ApiException("该订单在" + invalidDay + "不可用");
                }
            }
        }
        consume = new Consume();
        consume.setConsumeCode(String.valueOf(UUIDGenerator.generateUUID()));
        consume.setOrder_id(order.getId());
        consume.setOrder_code(order.getOrderCode());
        consume.setSupplyConsumeSeqId(seqId);
        consume.setConsumeNumber(number);
        consume.setPrevNumber(availableNumber);
        consume.setAfterNumber(consume.getPrevNumber() - consume.getConsumeNumber());
        consume.setConsumeTime(consumeTime);
        consume.setAddress(address);
        consume.setDeviceId(deviceId);
        consume.setSupply_id(order.getSupply_id());
        consume.setSupplyprod_id(order.getSupplyProdId());
        consume.setPlatform_id(order.getPlatform_id());
        consume.setPlatformprod_id(order.getPlatformprod_id());
        consume.setReverseStatus(false);
        consume.setCreateTime(new Date());
        consumeMapper.insertSelective(consume);
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setConsume(order.getConsume() + consume.getConsumeNumber());
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        return consume.getId();
    }
}
