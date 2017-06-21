package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.ReverseMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Reverse;
import com.all580.voucherplatform.utils.sign.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Linv2 on 2017-06-19.
 */
@Component
@Scope(value = "prototype")
@Slf4j
public class OrderReverseManager {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private ReverseMapper reverseMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;


    private Order order;
    private Consume consume;

    public void setConsume(String orderCode, String consumeSeqId) {
        order = orderMapper.selectByOrderCode(orderCode);
        consume = consumeMapper.selectBySeqId(order.getId(), order.getOrderCode(), order.getSupply_id(), consumeSeqId);
    }

    public void reverse(String reverseSqlId, Date reverseTime) {
        if (reverseMapper.selectBySeqId(order.getId(), order.getOrderCode(), order.getSupply_id(), reverseSqlId) != null) {
            log.debug("重复的冲正请求");
            return;
        }
        Reverse reverse = new Reverse();
        reverse.setOrder_id(order.getId());
        reverse.setOrder_code(order.getOrderCode());
        reverse.setConsume_id(consume.getId());
        reverse.setReverseSeqId(reverseSqlId);
        reverse.setReverseTime(reverseTime);
        reverse.setCreateTime(new Date());
        reverse.setPlatformprod_id(order.getPlatform_id());
        reverse.setPlatformprod_id(order.getPlatformprod_id());
        reverse.setSupply_id(order.getSupply_id());
        reverse.setSupplyprod_id(order.getSupplyProdId());
        reverseMapper.insertSelective(reverse);
        Consume updateConsume = new Consume();
        updateConsume.setId(consume.getId());
        updateConsume.setReverseStatus(true);
        consumeMapper.updateByPrimaryKeySelective(updateConsume);

        Order updateOrder = new Order();
        Integer reverseNum = order.getReverse() == null ? 0 : order.getReverse();
        reverseNum += consume.getConsumeNumber();
        updateOrder.setReverse(reverseNum);
        notifyPlatform(order.getPlatform_id(), reverse.getId());
    }

    private void notifyPlatform(final Integer platformId, final Integer reverseId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.reverse(reverseId);
                }
            }
        });
    }
}
