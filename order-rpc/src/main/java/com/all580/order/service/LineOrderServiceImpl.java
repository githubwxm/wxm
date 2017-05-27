package com.all580.order.service;

import com.all580.order.api.service.LineOrderService;
import com.all580.order.dao.LineGroupMapper;
import com.framework.common.Result;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by xiangzw on 2017/5/24.
 */
@Service
@Slf4j
public class LineOrderServiceImpl implements LineOrderService {

    @Autowired
    private LineGroupMapper lineGroupMapper;

    /**
     * 查询线路团队列表
     *
     * @param params
     * @return
     */
    @Override
    public Result<?> listGroup(Map params) {
        PageRecord<Map> record = new PageRecord<>();
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);

        int count = lineGroupMapper.countLineGroup(params);
        record.setTotalCount(count);

        if(count == 0){
            record.setList(Collections.EMPTY_LIST);
            return result;
        }
        //分页查询
        List<Map> list = lineGroupMapper.listGroup(params);
        record.setList(list);
        return result;
    }

    /**
     * 线路团队详情接口
     *
     * @param number
     * @param epId
     * @return
     */
    @Override
    public Result<?> getLineGroupDetailByNumber(String number, String epId) {
        Result<Map> result = new Result<>(true);
        Map map = lineGroupMapper.getLineGroupDetailByNumber(number, epId);
        result.put(map);
        return result;
    }

    /**
     * 线路订单游客列表
     *
     * @param groupNumber
     * @param epId
     * @param record_start
     * @param record_count
     * @return
     */
    @Override
    public Result<?> listOrderVisitor(String groupNumber, String epId, int record_start, int record_count) {
        PageRecord<Map> record = new PageRecord<>();
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(record);

        int count = lineGroupMapper.countLineGroupVisitorByGroupNumber(groupNumber, epId);
        record.setTotalCount(count);
        if(count == 0){
            record.setList(Collections.EMPTY_LIST);
            return result;
        }

        List<Map> visitorList = lineGroupMapper.getLineOrderVisitorsByNumber(groupNumber,epId,record_start,record_count);
        Collection<String> orderNumbers = new HashSet<>();
        Map<String,List<Map>> orderVisitorMap = new HashMap<>();
        for (Map visitor : visitorList) {
            String orderNumber = String.valueOf(visitor.get("order_number"));
            orderNumbers.add(orderNumber);//订单号
            //将游客按照订单分组
            if(orderVisitorMap.containsKey(orderNumber)){
                orderVisitorMap.get(orderNumber).add(visitor);
            }else{
                orderVisitorMap.put(orderNumber,Arrays.asList(visitor));
            }
        }

        List<Map> orderList = lineGroupMapper.getLineOrdersByNumbers(epId, groupNumber, orderNumbers);
        for (Map order : orderList) {
            String orderNumber = String.valueOf(order.get("number"));
            order.put("visitors",orderVisitorMap.get(orderNumber));
        }
        record.setList(orderList);
        return result;
    }
}
