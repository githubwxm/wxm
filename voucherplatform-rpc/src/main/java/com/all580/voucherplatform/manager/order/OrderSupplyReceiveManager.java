package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.manager.OrderMessageManager;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-14.
 */

@Slf4j
@Service
public class OrderSupplyReceiveManager {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private OrderMessageManager orderMessageManager;

    /**
     * @param mapList {orderId:xx,orderCode:xx,supplyOrderId:xx}
     *                orderId  int 订单id
     *                orderCode  string 订单号
     *                supplyOrderId    string  票务订单id
     */
    public void submit(List<Map> mapList) throws Exception {
        Integer[] orderIds = new Integer[mapList.size()];
        Integer platformId = null;
        for (int i = 0; i < mapList.size(); i++) {
            Map map = mapList.get(i);
            Order order = null;
            if (map.containsKey("orderId")) {
                Integer orderId = CommonUtil.objectParseInteger(map.get("orderId"));
                order = orderMapper.selectByPrimaryKey(orderId);
            } else if (map.containsKey("orderCode")) {
                String orderCode = CommonUtil.objectParseString(map.get("orderCode"));
                order = orderMapper.selectByOrderCode(orderCode);
            }
            if (order == null) {
                throw new Exception("供应商订单确认---订单数据不存在");
            }
            if (platformId == null) {
                platformId = order.getPlatform_id();
            }
            orderIds[i] = order.getId();
            String supplyOrderId = CommonUtil.objectParseString(map.get("supplyOrderId"));
            Receive(order, supplyOrderId);
        }
        notifyPlatform(platformId, orderIds);
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW)
    private void Receive(Order order, String supplyOrderId) {
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setSupplyOrderId(supplyOrderId);
        orderMapper.updateByPrimaryKeySelective(updateOrder);
        orderMessageManager.sendOrderMessage(order);
    }

    public void notifyPlatform(final Integer platformId, final Integer... orderId) {
        asyncService.run(new Runnable() {
            @Override
            public void run() {
                PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(platformId);
                if (platformAdapterService != null) {
                    platformAdapterService.sendOrder(orderId);
                }
            }
        });
    }

}
