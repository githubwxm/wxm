package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.ConsumeMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.ReverseMapper;
import com.all580.voucherplatform.entity.Consume;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.Reverse;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
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

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;


    private Order order;
    private Consume consume;

    public void setConsume(String orderCode,
                           String consumeSeqId) {
        order = orderMapper.selectByOrderCode(orderCode);
        consume = consumeMapper.selectBySeqId(order.getId(), order.getOrderCode(), order.getSupply_id(), consumeSeqId);
    }

    public void setConsume(Integer consumeId) {
        consume = consumeMapper.selectByPrimaryKey(consumeId);
        if (consume == null) {
            throw new ApiException("要冲正的验证数据不存在");
        }
        order = orderMapper.selectByPrimaryKey(consume.getOrder_id());
    }

    public void submit(String reverseSqlId,
                       Date reverseTime) {
        DistributedReentrantLock distributedReentrantLock = distributedLockTemplate.execute(
                VoucherConstant.DISTRIBUTEDLOCKORDER + order.getOrderCode(), lockTimeOut);
        try {
            Integer reverseId = reverse(reverseSqlId, reverseTime);
            notifyPlatform(order.getPlatform_id(), reverseId);
        } catch (ApiException e) {
            throw e;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new ApiException("系统异常");
        } finally {
            distributedReentrantLock.unlock();
        }

    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private Integer reverse(String reverseSqlId,
                            Date reverseTime) throws Exception {
        if (consume.getReverseStatus()) {
            throw new ApiException("验证数据已被冲正");
        }
        if (reverseMapper.selectBySeqId(order.getId(), order.getOrderCode(), order.getSupply_id(),
                consume.getId()) != null) {
            log.debug("重复的冲正请求");
            throw new ApiException("重复的冲正请求");
        }
        Reverse reverse = new Reverse();
        reverse.setOrder_id(order.getId());
        reverse.setOrder_code(order.getOrderCode());
        reverse.setConsume_id(consume.getId());
        reverse.setConsume_code(consume.getConsumeCode());
        reverse.setReverseSeqId(reverseSqlId);
        reverse.setReverseTime(reverseTime);
        reverse.setCreateTime(new Date());
        reverse.setPlatform_id(order.getPlatform_id());
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
        updateOrder.setId(order.getId());
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        return reverse.getId();
    }

    private void notifyPlatform(final Integer platformId,
                                final Integer reverseId) {
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
