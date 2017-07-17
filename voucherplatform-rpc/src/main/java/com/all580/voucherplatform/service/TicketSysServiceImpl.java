package com.all580.voucherplatform.service;

import com.all580.voucherplatform.api.service.TicketSysService;
import com.all580.voucherplatform.dao.TicketSysMapper;
import com.all580.voucherplatform.entity.TicketSys;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import com.framework.common.vo.PageRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Linv2 on 2017-05-19.
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@Slf4j
public class TicketSysServiceImpl implements TicketSysService {

    @Autowired
    private TicketSysMapper mapper;


    @Override
    public Result<PageRecord<Map>> selectTicketSysList(Integer recordStart, Integer recordCount) {
        PageRecord<Map> pageRecord = new PageRecord<>();
        int count = mapper.selectTicketSysCount();
        pageRecord.setTotalCount(count);
        if (count > 0) {
            pageRecord.setList(mapper.selectTicketSysList(recordStart, recordCount));
        } else {
            pageRecord.setList(new ArrayList<Map>());
        }
        Result<PageRecord<Map>> result = new Result<>(true);
        result.put(pageRecord);
        return result;
    }
}
