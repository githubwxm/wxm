package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.LockPayManagerService;
import com.all580.payment.dao.CapitalSerialMapper;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wxming on 2017/1/10 0010.
 */
@Service("lockPayManagerService")
public class LockPayManagerServiceImpl implements LockPayManagerService {


    @Autowired
    private CapitalSerialMapper capitalSerialMapper;
    @Autowired
    private BalancePayService balancePayService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;

    @Override
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type, String serialNum) {
        List<DistributedReentrantLock> listLock= new ArrayList<>();
        try{
            Set<String> set = new HashSet<>();
            for(BalanceChangeInfo c:balanceChangeInfoList){
                set.add(String.valueOf(c.getEp_id())+String.valueOf(c.getCore_ep_id()));
            }
            listLock.add(distributedLockTemplate.execute( type+serialNum, lockTimeOut));
            int ref=   capitalSerialMapper.selectSerialNumExists(type,serialNum);
            if(ref>0){
                return new Result(false,"类型:"+ type+ "流水:" +serialNum + "重复操作");
            }
            for(String temp:set){
                listLock.add(distributedLockTemplate.execute( temp, lockTimeOut));
            }
            balancePayService.changeBalances(balanceChangeInfoList,type,serialNum);
        }catch (Exception e){

        }finally {
            for(DistributedReentrantLock disLock:listLock){
                disLock.unlock();
            }
        }
        return null;
    }
}
