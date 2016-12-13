package com.all580.order.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.dao.OrderClearanceSerialMapper;
import com.all580.order.dao.OrderItemMapper;
import com.all580.order.dao.OrderMapper;
import com.all580.order.entity.Order;
import com.framework.common.Result;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/10/22 17:32
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderClearanceSerialMapper orderClearanceSerialMapper;

    @Override
    public Result<Integer> getPayeeEpIdByOutTransId(String outTranId) {
        Order order = orderMapper.selectByThirdSn(outTranId);
        if (order == null) {
            return new Result<>(false, "订单不存在");
        }
        Result<Integer> result = new Result<>(true);
        result.put(order.getPayee_ep_id());
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectPlatformOrderBySupplierCore(Integer coreEpId, Date startTime, Date endTime, Integer orderStatus, Integer orderItemStatus, String phone, Long orderItemNumber, Integer recordStart, Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = orderItemMapper.selectPlatformBySupplierCoreEpIdCount(coreEpId, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNumber);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = orderItemMapper.selectPlatformBySupplierCoreEpId(coreEpId, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNumber, recordStart, recordCount);
            record.setList(list);
        } else {
            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result<PageRecord<Map>> selectBySupplierPlatform(Integer coreEpId, Integer saleCoreEpId, Integer dateType, Date startTime, Date endTime, Integer orderStatus, Integer orderItemStatus, String phone, Long orderItemNum, Boolean self, Long productSubNumber, Integer recordStart, Integer recordCount) {
        PageRecord<Map> record = new PageRecord<>();
        int count = orderItemMapper.selectBySupplierPlatformCount(coreEpId, saleCoreEpId, dateType, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNum, self, productSubNumber);
        record.setTotalCount(count);
        if (count > 0) {
            List<Map> list = orderItemMapper.selectBySupplierPlatform(coreEpId, saleCoreEpId, dateType, startTime, endTime, orderStatus, orderItemStatus, phone, orderItemNum, self, productSubNumber, recordStart, recordCount);
            record.setList(list);
        } else {
            record.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);
        return result;
    }

    @Override
    public Result<List<Map>> selectChannelBill(Integer coreEpId, Date start, Date end, Boolean settled) {
        List<Map> list = orderClearanceSerialMapper.selectChannelBill(coreEpId, start, end, settled);
        Result<List<Map>> result = new Result<>(true);
        result.put(list);
        return result;
    }

    @Override
    public Result settledChannelBill(Map params) {

        return new Result(true);
    }
}
