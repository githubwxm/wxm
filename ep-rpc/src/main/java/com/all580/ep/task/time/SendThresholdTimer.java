package com.all580.ep.task.time;

import com.all580.ep.com.Common;
import com.all580.ep.dao.EpMapper;
import com.all580.ep.dao.SmsSendMapper;
import com.all580.notice.api.conf.SmsType;
import com.all580.notice.api.service.SmsService;
import com.all580.payment.api.service.BalancePayService;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.DateFormatUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;



/**
 * 检查余额不足发送短信， 按定时器配置  @Scheduled(cron = "0 59 9 * * ?")  每天9点59启动  执行一次
 * Created by wxming on 2017/5/11 0011.
 */
@Component
@Slf4j
public class SendThresholdTimer {

    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;
    private final  String key ="threshold_key";
    @Autowired
    SmsSendMapper smsSendMapper;
    @Autowired
    BalancePayService balancePayService;
    @Autowired
    private EpMapper epMapper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisUtils redisUtils;
    @Scheduled(cron = "0 59 09 * * ?")
   //@Scheduled(fixedDelay = 60000*10)
    public void sendThresholdTimerJob() {
        //ep_id,core_ep_id,balance
       List<DistributedReentrantLock> listLock= new ArrayList<>();
       try{
           Date date = new Date();
           String current = DateFormatUtils.converToStringDate(date);
           listLock.add(distributedLockTemplate.execute(current, lockTimeOut));
           String redis_date =  redisUtils.get(key);
           if(redis_date==null || DateFormatUtils.converToDate(current).after(DateFormatUtils.converToDate(redis_date))){
               thresholdSend(current);
           }else{
            log.info("{} 已经发送过余额告警短信",redis_date);
           }
       } finally {
           for(DistributedReentrantLock disLock:listLock){
               disLock.unlock();
           }
       }


    }
    private void thresholdSend(String current){
        redisUtils.set("threshold_key",current);
        Map<String,Object> balanceMap = new HashMap<>();
        List<Map<String,Object>> thresholdList= smsSendMapper.selectThreshold();
        if( thresholdList!=null && !thresholdList.isEmpty()){
            List<Map<String,Object>> balanceList=  balancePayService.listCapitalAll().get();
            for(Map<String,Object> tempMap:balanceList){
                balanceMap.put(tempMap.get("core_ep_id")+"-"+tempMap.get("ep_id"),tempMap.get("balance"));
            }
            for(Map<String,Object> mapSend: thresholdList){
                Integer threshold = CommonUtil.objectParseInteger(mapSend.get("threshold"));
                Integer balance = CommonUtil.objectParseInteger(balanceMap.get(mapSend.get("core_ep_id")+"-"+mapSend.get("ep_id")));
                if(threshold>balance){//发送短信
                    Map<String,Object> epMap=   epMapper.selectId(CommonUtil.objectParseInteger(mapSend.get("ep_id")));
                    Map<String, String>  params = new HashMap<>();
                    params.put("qiye",epMap.get("name").toString());
                    String jinqian =threshold+"";
                    if(jinqian.length()>2){
                        String point = Common.matcher(jinqian,"([\\d]{2}$)" );//Common.matcher(125665419+"","([\\d]{2}$)" )
                        params.put("jinqian",jinqian.replaceAll("([\\d]{2}$)", "."+point));
                    }else if(jinqian.length()==2){
                        params.put("jinqian","0."+jinqian);
                    }else if(jinqian.length()==1){
                        params.put("jinqian","0.0"+jinqian);
                    }
                    Integer core_ep_id=CommonUtil.objectParseInteger(mapSend.get("core_ep_id"));
                    String send_phone2=CommonUtil.objectParseString(mapSend.get("send_phone2"));
                    if( null != send_phone2 && !"".equals(send_phone2.trim())){
                        smsService.send(send_phone2, SmsType.Ep.BALANCE_SHORTAGE,core_ep_id,params);//发送短信
                    }
                    String send_phone1=CommonUtil.objectParseString(mapSend.get("send_phone1"));
                    if(  null != send_phone1 && !"".equals(send_phone1.trim())){
                        smsService.send(send_phone1, SmsType.Ep.BALANCE_SHORTAGE,core_ep_id,params);//发送短信
                    }
                    smsSendMapper.sendUpdateNumber(mapSend);
                }
            }
        }
    }
}
