package com.all580.payment.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Map;

/**
 * Created by wxming on 2016/12/13 0013.
 */
public interface FundSerialService {
    Result updateFundSerialSummary(Map<String,Object> params);
    Result insertFundSerial(Map<String,Object> params);
    Result<PageRecord<Map<String, Object>>> selectFundSerial(Integer core_ep_id,
                                                             String status,
                                                             String start_date,
                                                             String end_date,
                                                             String ref_id,
                                                             Integer start_record,
                                                             Integer max_records, Integer export);
}
