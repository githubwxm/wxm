package com.all580.voucherplatform.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Map;

/**
 * Created by Linv2 on 2017/5/12.
 */
public interface TicketSysService {
    Result<PageRecord<Map>> selectTicketSysList(Integer recordStart,   Integer recordCount);
}
