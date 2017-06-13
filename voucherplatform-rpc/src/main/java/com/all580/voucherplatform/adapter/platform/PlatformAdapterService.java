package com.all580.voucherplatform.adapter.platform;

import com.all580.voucherplatform.adapter.AbstractProcessorAdapter;
import com.all580.voucherplatform.entity.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by Linv2 on 2017-06-07.
 */
@Slf4j
@Service
public abstract class PlatformAdapterService extends AbstractProcessorAdapter<Platform> {


    public abstract Object sendOrder(Integer... orderIds);

    public abstract Object consumeTicketRet(Integer consumeId);

    public abstract Object refundOrder(Integer refundId);

    public abstract Object reverse(Integer reverseId);

    public abstract Object update(Integer orderId);


    public abstract Object sendGroupOrder(Integer groupOrderId);

    public abstract Object refundGroup(Integer groupOrderId);

    public abstract Object updateGroup(Integer groupOrderId);

    public abstract Object activateGroupOrder(Integer groupOrderId);

    public abstract Object reverseGroupOrder(Integer groupOrderId);
}
