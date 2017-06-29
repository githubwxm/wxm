package com.all580.voucherplatform.api.service;

import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017/5/12.
 */
public interface TicketSysService {

    int getCount();

    List<Map> getList(int recordStart, int recordCount);
}
