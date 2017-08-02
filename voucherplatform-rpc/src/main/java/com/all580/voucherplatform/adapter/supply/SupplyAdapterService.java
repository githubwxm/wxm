package com.all580.voucherplatform.adapter.supply;

import com.all580.voucherplatform.adapter.AbstractProcessorAdapter;
import com.all580.voucherplatform.dao.PlatformMapper;
import com.all580.voucherplatform.dao.SupplyMapper;
import com.all580.voucherplatform.entity.Platform;
import com.all580.voucherplatform.entity.Supply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/15.
 */
@Service
@Slf4j
public abstract class SupplyAdapterService extends AbstractProcessorAdapter<Supply> {


    public abstract Map getConf(Integer supplyId);


    public abstract void queryProd(Integer supplyId);

    public abstract void sendOrder(Integer... orderId);

    public abstract void sendGroupOrder(Integer groupOrderId);

    public abstract void queryOrder(Integer orderId);

    public abstract void consume(Integer consumeId);

    public abstract void refund(int refundId);

    public abstract void update(Integer orderId);

    public abstract void refundGroup(Integer groupRefId);

    public abstract void updateGroup(Integer groupOrderId, String... seqId);

}
