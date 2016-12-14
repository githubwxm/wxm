package com.all580.ep.service;

import com.all580.ep.dao.PlatfromFundMapper;
import com.all580.payment.api.service.PlatfromFundService;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by wxming on 2016/12/13 0013.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class PlatfromFundServiceImpl implements PlatfromFundService {

    @Autowired
    private PlatfromFundMapper platfromFundMapper;
    @Override
    public Result<Map<String, Object>> selectPlatfromFund(int core_ep_id) {
        Result<Map<String, Object>> result= new Result<>();
        result.put(platfromFundMapper.selectPlatfromFund(core_ep_id));
        result.setSuccess();
        return result;
    }
    @Override
    public Result<Integer> insertPlatfromFund(int core_ep_id) {
        Result<Integer> result= new Result<>();
        result.put(platfromFundMapper.insertPlatfromFund(core_ep_id));
        result.setSuccess();
        return result;
    }
    @Override
    public Result<Integer> exitFund(int money,int core_ep_id) {
        Result<Integer> result= new Result<>();
        result.put(platfromFundMapper.exitFund(money,core_ep_id));
        result.setSuccess();
        return result;
    }
    @Override
    public Result<Integer> addFund(int money,int core_ep_id) {
        Result<Integer> result= new Result<>();
        result.put(platfromFundMapper.addFund(money,core_ep_id));
        result.setSuccess();
        return result;
    }


}
