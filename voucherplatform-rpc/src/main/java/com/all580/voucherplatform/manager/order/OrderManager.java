package com.all580.voucherplatform.manager.order;

import com.all580.voucherplatform.adapter.AdapterLoader;
import com.all580.voucherplatform.adapter.platform.PlatformAdapterService;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.dao.GroupOrderMapper;
import com.all580.voucherplatform.dao.OrderMapper;
import com.all580.voucherplatform.dao.SupplyProductMapper;
import com.all580.voucherplatform.entity.GroupOrder;
import com.all580.voucherplatform.entity.Order;
import com.all580.voucherplatform.entity.SupplyProduct;
import com.all580.voucherplatform.manager.order.grouporder.ConsumeGroupOrderManager;
import com.all580.voucherplatform.utils.async.AsyncService;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class OrderManager {
    @Autowired
    private AdapterLoader adapterLoader;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private ConsumeOrderManager consumeOrderManager;
    @Autowired
    private ConsumeGroupOrderManager consumeGroupOrderManager;
    @Autowired
    public OrderMapper orderMapper;
    @Autowired
    private GroupOrderMapper groupOrderMapper;
    @Autowired
    public SupplyProductMapper supplyProductMapper;

    @Value("${lock.timeout}")
    private int lockTimeOut = 3;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    public Integer submitConsume(String seqId,
                                 Integer number,
                                 String address,
                                 Date consumeTime,
                                 String deviceId, final Order order) {

        DistributedReentrantLock distributedReentrantLock = distributedLockTemplate.execute(
                VoucherConstant.DISTRIBUTEDLOCKORDER + order.getOrderCode(), lockTimeOut);
        try {
            final Integer consumeId = consumeOrderManager.consume(seqId, number, address, consumeTime, deviceId, order);
            asyncService.run(new Runnable() {
                @Override
                public void run() {
                    try {
                        PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(order.getPlatform_id());
                        if (platformAdapterService != null) {
                            platformAdapterService.consume(consumeId);
                        }
                    } catch (Throwable e) {
                        log.error("异步调用异常", e);
                    }
                }
            });
            return consumeId;
        } catch (Throwable ex) {
            log.error("核销订单异常", ex);
            throw ex;
        } finally {
            distributedReentrantLock.unlock();
        }
    }

    public Integer submitConsume(Map params, Order order) {
        String consumeSeqId = CommonUtil.objectParseString(params.get("consumeSeqId"));
        String voucherId = CommonUtil.objectParseString(params.get("voucherId"));
        Date consumeTime = DateFormatUtils.converToDateTime(CommonUtil.objectParseString(params.get("consumeTime")));
        Integer consumeNumber = CommonUtil.objectParseInteger(params.get("consumeNumber"));
        String consumeAddress = CommonUtil.objectParseString(params.get("consumeAddress"));
        String deviceId = CommonUtil.objectParseString(params.get("deviceId"));
        if (order == null) order = getOrder(voucherId);
        return submitConsume(consumeSeqId, consumeNumber, consumeAddress, consumeTime, deviceId, order);
    }

    public Integer submitConsume(Map params) {
        return submitConsume(params, null);
    }

    public void submitConsume(Integer number, String voucherId, final GroupOrder groupOrder, String idNumbers) {
        DistributedReentrantLock lock = distributedLockTemplate.execute(voucherId, lockTimeOut);
        try {
            if (idNumbers == null || idNumbers.equals("[]")) {
                consumeGroupOrderManager.consume(number, groupOrder);
            } else {
                consumeGroupOrderManager.consume(number, groupOrder, StringUtils.split(idNumbers, ","));
            }
            asyncService.run(new Runnable() {
                @Override
                public void run() {
                    try {
                        PlatformAdapterService platformAdapterService = adapterLoader.getPlatformAdapterService(groupOrder.getPlatform_id());
                        if (platformAdapterService != null) {
                            platformAdapterService.activateGroupOrder(groupOrder.getId());
                        }
                    } catch (Throwable e) {
                        log.error("异步调用异常", e);
                    }
                }
            });
        } catch (Throwable e) {
            log.error("核销订单异常", e);
            throw e;
        } finally {
            lock.unlock();
        }
    }

    public void submitConsumeGroup(Map params) {
        String voucherId = CommonUtil.objectParseString(params.get("voucherId"));
        String idNumbers = CommonUtil.objectParseString(params.get("idNumbers"));
        Map mapProd = (Map) params.get("product");
        Integer number = CommonUtil.objectParseInteger(mapProd.get("number"));
        submitConsume(number, voucherId, getGroupOrder(voucherId), idNumbers);
    }

    public Order getOrder(String orderCode) {
        return orderMapper.selectByOrderCode(orderCode);
    }

    public Order getOrder(Integer orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    public Order getOrder(Integer supplyId,
                          String supplyOrderId) throws Exception {
        return orderMapper.selectBySupply(supplyId, supplyOrderId);
    }

    public GroupOrder getGroupOrder(String orderCode) {
        return groupOrderMapper.selectByOrderCode(orderCode);
    }

    public Map getMap(Integer... orderId) throws Exception {
        List<Order> orderList = orderMapper.selectOrderListByPrimaryKey(orderId);
        Map map = new HashMap();
        String batchNo = orderList.get(0).getOrderCode();
        if (orderList.get(0).getSupplyProdId() == null) {
            log.debug("订单号为{}未绑定票务产品", new Object[]{orderList.get(0).getOrderCode()});
            throw new Exception("订单未绑定票务产品");
        }
        SupplyProduct supplyProduct = supplyProductMapper.selectByPrimaryKey(orderList.get(0).getSupplyProdId());

        if (supplyProduct == null) {
            log.debug("订单号为{}未绑定票务产品", new Object[]{orderList.get(0).getOrderCode()});
            throw new Exception("订单未绑定票务产品");
        }
        map.put("batch", batchNo);
        map.put("orders", getVisitorMap(orderList, supplyProduct.getCode()));
        map.put("supplyId", supplyProduct.getSupply_id());
        return map;
    }

    private List<Map> getVisitorMap(List<Order> orderList, String prodId) {
        List<Map> mapList = new ArrayList<>();
        for (Order order : orderList) {
            Map mapOrder = new HashMap();
            mapOrder.put("voucherId", order.getOrderCode());
            mapOrder.put("otaOrderId", order.getPlatformOrderId());
            mapOrder.put("productId", prodId);
            mapOrder.put("payment", 1);
            mapOrder.put("consumeType", order.getConsumeType());
            mapOrder.put("validTime", DateFormatUtils.converToStringTime(order.getValidTime()));
            mapOrder.put("invalidTime", DateFormatUtils.converToStringTime(order.getInvalidTime()));
            mapOrder.put("validWeek", order.getValidWeek());
            mapOrder.put("invalidDate", order.getInvalidDate());
            mapOrder.put("qrCode", order.getVoucherNumber());
            mapOrder.put("customName", order.getCustomName());
            mapOrder.put("mobile", order.getMobile());
            mapOrder.put("idNumber", order.getIdNumber());
            mapOrder.put("number", order.getNumber());
            mapOrder.put("channel", order.getChannel());
            mapList.add(mapOrder);
        }
        return mapList;
    }
}
