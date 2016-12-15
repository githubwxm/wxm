package com.all580.ep.service;

import com.all580.ep.dao.FundSerialMapper;
import com.all580.payment.api.service.FundSerialService;
import com.framework.common.Result;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.exception.ApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/12/13 0013.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class FundSerialServiceImpl implements FundSerialService{

    @Autowired
    private FundSerialMapper fundSerialMapper;


    @Override
    public Result updateFundSerialSummary(Map<String,Object> params){
        try {
            fundSerialMapper.updateFundSerialSummary(params);
            return new Result(true);
        } catch (Exception e) {
            log.error("修改备注数据异常", e);
            throw new ApiException("修改备注数据异常", e);
        }
    }
    @Override
    public Result insertFundSerial(Map<String,Object> params){
        try {
            fundSerialMapper.insertFundSerial(params);
            return new Result(true);
        } catch (Exception e) {
            log.error("添加平台资金流水异常", e);
            throw new ApiException("添加平台资金流水异常", e);
        }
    }

    @Override
    public Result<Integer> selectExists(Map<String,Object> params){
        Result<Integer> result = new  Result(true);
        try {

            result.put( fundSerialMapper.selectExists(params));
            return result;
        } catch (Exception e) {
            log.error("添加平台资金流水异常", e);
            throw new ApiException("添加平台资金流水异常", e);
        }
    }
    @Override
    public  Result<PageRecord<Map<String, Object>>> selectFundSerial( Integer core_ep_id,
                                              String status,
                                               String start_date,
                                             String end_date,
                                              String ref_id,
                                              Integer start_record,
                                             Integer max_records,Integer export){
        Result<PageRecord<Map<String, Object>>> result = new Result<>();
        try {
         int count=    fundSerialMapper.selectFundSerialCount( core_ep_id,status, start_date,
                     end_date, ref_id,start_record, max_records);
            List<Map<String,Object>> list=null;
            if(count!=0){
                if(export==null){
                    list =fundSerialMapper.selectFundSerial(core_ep_id,status, start_date,
                            end_date, ref_id,start_record, max_records);
                }else{
                    list =fundSerialMapper.selectFundSerialExport(core_ep_id,status, start_date,
                            end_date, ref_id,start_record, max_records);
                }
            }
            PageRecord<Map<String, Object>> record = new PageRecord<>(count, list);
            result.setSuccess();
            result.put(record);

            return result;
        } catch (Exception e) {
            log.error("查询平台资金流水异常", e);
            throw new ApiException("查询平台资金流水异常", e);
        }
    }


//    int updateFundSerialSummary(@Param("id") Integer id,@Param("summary") String summary);
//    int insertFundSerial(Map<String,Object> params);
//    List<Map<String,Object>> selectFundSerial(@Param("core_ep_id") Integer core_ep_id,
//                                              @Param("status") String status,
//                                              @Param("start_date") String start_date,
//                                              @Param("end_date") String end_date,
//                                              @Param("ref_id") String ref_id,
//                                              @Param("start_record")Integer start_record,
//                                              @Param("max_records")Integer max_records);

}
