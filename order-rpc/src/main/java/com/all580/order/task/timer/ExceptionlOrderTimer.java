package com.all580.order.task.timer;

import com.all580.ep.api.service.EpService;
import com.all580.order.dao.OrderExceptionMapper;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.*;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 取消订单定时器
 * @date 2016/10/13 16:26
 */
@Component
@Slf4j
public class ExceptionlOrderTimer {
    public static final Long interval=1000L*60*20;
    @Autowired
    private OrderExceptionMapper orderExceptionMapper;
    @Autowired
    private EpService epService;
    @Scheduled(fixedDelay =1000)
    public void exceptionOrderTimerJob() {

        Long start= new Date().getTime();
        List<Map<String,Object>> list = orderExceptionMapper.selectOrderSendException();
        List<Map<String,Object>> list1 = orderExceptionMapper.selectOrderFefundException();
        List<Map<String,Object>> list2 = orderExceptionMapper.selectOrderNumException();
        list.addAll(list1);
        list.addAll(list2);
        if(!list.isEmpty()){
           List<Integer> epIdList = new ArrayList<>();
            for(Map<String,Object>map:list){
                Integer epId= CommonUtil.objectParseInteger(map.get("supplier_ep_id")) ;
                if(!epIdList.contains(epId)){
                    epIdList.add(epId);
                }
            }

            Result<List<Map<String, Object>>> epResult = epService.getEp(epIdList.toArray(new Integer[epIdList.size()]), new String[] {"id","name"});
            if (epResult == null || epResult.isFault()) throw new ApiException("没有可上架的企业");
            List<Map<String, Object>> eps = epResult.get();
            Map<Integer,String> epMap = new HashMap<>();
            for(Map<String,Object> tempMap :eps){
                String name = CommonUtil.objectParseString( tempMap.get("name"))  ;
                Integer id=CommonUtil.objectParseInteger( tempMap.get("id"));
                epMap.put(id,name);
            }
            for(Map<String,Object>map:list){
                Integer epId= CommonUtil.objectParseInteger(map.get("supplier_ep_id")) ;
                map.put("supplier_ep_name",epMap.get(epId));
            }
            orderExceptionMapper.insertOrderException(list);
        }
        Long end= new Date().getTime();
        Long operateTime =end-start;
        if(operateTime-interval>0){
           log.info("异常订单操作时间大于20分钟 用时{}秒",operateTime);
        }else{
            log.info("异常订单操作时间{}秒",(operateTime/1000));
            try {
                Thread.sleep(interval-operateTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
