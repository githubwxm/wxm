package com.all580.role.service;

import com.all580.role.api.service.IntfService;
import com.all580.role.dao.IntfMapper;
import com.framework.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.Map;

/**
 * Created by wxming on 2016/11/10 0010.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class IntfServiceImple implements IntfService {

    @Autowired
    private IntfMapper intfMapper;



    @Override
    public Result insertInft(Map<String, Object> params) {
        try {
            intfMapper.insertIntf(params);
           // funcIntfMapper.insertFuncIntf(params);
        } catch (Exception e) {
            log.error("添加接口异常", e);
            new ApiException("添加接口异常");
        }
        return new Result(true);
    }
    @Override
    public Result selectFuncId(int id) {
        try {
            intfMapper.selectFuncId(id);
            // funcIntfMapper.insertFuncIntf(params);
        } catch (Exception e) {
            log.error("查询接口异常", e);
            new ApiException("查询接口异常");
        }
        return new Result(true);
    }

    @Override
    public Result deleteInft(int id) {
        try {
           // funcIntfMapper.deleteFuncIntf(id);

            intfMapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            log.error("删除接口异常", e);
            new ApiException("删除接口异常");
        }
        return new Result(true);
    }
}