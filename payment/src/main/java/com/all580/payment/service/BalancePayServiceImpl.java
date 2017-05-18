package com.all580.payment.service;

import com.all580.payment.api.model.BalanceChangeInfo;
import com.all580.payment.api.model.BalanceChangeRsp;
import com.all580.payment.api.service.BalancePayService;
import com.all580.payment.api.service.LockPayManagerService;
import com.all580.payment.dao.CapitalMapper;
import com.all580.payment.dao.CapitalSerialMapper;
import com.all580.payment.entity.Capital;
import com.framework.common.Result;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.vo.PageRecord;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.*;

/**
 * 余额支付的实现类
 *
 * @author Created by panyi on 2016/9/28.
 */
@Service("balancePayService")
public class BalancePayServiceImpl implements BalancePayService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LockPayManagerService lockPayManagerService;
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;
    @Value("${lock.timeout}")
    private int lockTimeOut = 3;
    @Autowired
    private CapitalMapper capitalMapper;
    @Autowired
    private CapitalSerialMapper capitalSerialMapper;

    @Override
    public Result<BalanceChangeRsp> changeBalances(List<BalanceChangeInfo> balanceChangeInfoList, Integer type,
                                                   final String serialNum) {
        List<DistributedReentrantLock> listLock= new ArrayList<>();
        try{
            Set<String> set = new HashSet<>();
            for(BalanceChangeInfo c:balanceChangeInfoList){
                set.add(String.valueOf(c.getEp_id())+String.valueOf(c.getCore_ep_id()));
            }
            listLock.add(distributedLockTemplate.execute( type+serialNum, lockTimeOut));
            int ref=   capitalSerialMapper.selectSerialNumExists(type,serialNum);
            if(ref>0){
                return new Result<>(Result.UNIQUE_KEY_ERROR, "类型:"+ type+ "流水:" +serialNum + "重复操作");
            }
            for(String temp:set){
                listLock.add(distributedLockTemplate.execute( temp, lockTimeOut));
            }
            return   lockPayManagerService.changeBalances(balanceChangeInfoList,type,serialNum);
        }finally {
            for(DistributedReentrantLock disLock:listLock){
                disLock.unlock();
            }
        }
    }





    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result createBalanceAccount(Integer epId, Integer coreEpId) {
        Assert.notNull(epId, "参数【epId】不能为空");
        Assert.notNull(coreEpId, "参数【coreEpId】不能为空");
        Result result = new Result();
        Capital capital = null;
        capital= capitalMapper.selectByEpIdAndCoreEpId(epId,coreEpId);
        if(null==capital){
            capital=  new Capital();
            capital.setEp_id(epId);
            capital.setCore_ep_id(coreEpId);
            capitalMapper.insertSelective(capital);
        }
        result.setSuccess();
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public Result setCredit(Integer epId, Integer coreEpId, Integer credit) {
        Assert.notNull(epId, "参数【epId】不能为空");
        Assert.notNull(coreEpId, "参数【coreEpId】不能为空");
        Assert.notNull(credit, "参数【credit】不能为空");
        Result result = new Result();
        try {
            Capital capital = new Capital();
            capital.setEp_id(epId);
            capital.setCore_ep_id(coreEpId);
            capital.setCredit(credit);
            capitalMapper.updateByEpIdAndCoreEpId(capital);
            result.setSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setFail();
            result.setError(e.getMessage());
        }
        return result;
    }

    @Override
    public Result<Map<String, String>> getBalanceAccountInfo(Integer epId, Integer coreEpId) {
        Result<Map<String, String>> result = new Result<>();
        Capital capital = capitalMapper.selectByEpIdAndCoreEpId(epId, coreEpId);
        Assert.notNull(capital, MessageFormat.format("没有找到余额账户:epId={0}|coreEpId={1}", epId, coreEpId));
        try {
            Map<String, String> map = BeanUtils.describe(capital);
            result.put(map);
            result.setSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setFail();
            result.setError("转换出错：Capital -> Map");
        }
        return result;
    }

    @Override
    public Result<List<Map<String,Object>>> listCapitalAll(){
        Result result = new Result(true);
        result.put(capitalMapper.listCapitalAll());
        return  result;
    }
    @Override
    public Result<List<Map<String, String>>> getBalanceList(List<Integer> epIdList, Integer coreEpId) {
        logger.debug("开始 -> 批量获取余额账户");
        Result<List<Map<String, String>>> result = new Result<>();
        if (epIdList.size() > 100) {
            epIdList = epIdList.subList(0, 100);
        }
        List<Map<String, String>> list = capitalMapper.listByEpIdAndCoreEpId(epIdList, coreEpId);
        result.setSuccess();
        result.put(list);
        return result;
    }

    @Override
    public Result<PageRecord<Map<String, String>>> getBalanceSerialList(Integer epId, Integer coreEpId,
                                                                        String balanceSatatus,String startDate,String endDate,String ref_id,
                                                                        Integer export ,    Integer startRecord, Integer maxRecords,
                                                                        Integer type,Integer balance_type) {
        Result<PageRecord<Map<String, String>>> result = new Result<>();
        Capital capital = capitalMapper.selectByEpIdAndCoreEpId(epId, coreEpId);
        Assert.notNull(capital, MessageFormat.format("没有找到余额账户:epId={0}|coreEpId={1}", epId, coreEpId));
        List<Map<String, String>> capitalSerials=null;
        if(null==export){
           capitalSerials = capitalSerialMapper.listByCapitalId(capital.getId(),
                    balanceSatatus ,startDate,endDate,ref_id,startRecord, maxRecords,type,balance_type);
        }else{
            capitalSerials = capitalSerialMapper.listByCapitalIdExport(capital.getId(),
                    balanceSatatus ,startDate,endDate,ref_id,startRecord, maxRecords,type,balance_type);
        }

        int count = capitalSerialMapper.countByCapitalId(capital.getId(), balanceSatatus ,startDate,endDate,ref_id,type,balance_type);
        PageRecord<Map<String, String>> record = new PageRecord<>(count, capitalSerials);
        result.setSuccess();
        result.put(record);
        return result;
    }

    @Override
    public Result<Integer> updateSummary(int id,String summary){
        Result<Integer> result = new Result<>();
        int ref= capitalSerialMapper.updateSummary(id,summary);
        result.put(ref);
        result.setSuccess();
        return result;
    }

    public Result<Map<String,Object>> getBalanceType(){
        return  null;
    }
}
