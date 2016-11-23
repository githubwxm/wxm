package com.all580.notice.api.service;

import com.framework.common.Result;

import java.util.Date;

/**
 * Created by wxming on 2016/11/23 0023.
 */
public interface BalanceChangeSubscribeService {
    Result process(String mnsMsgId, String content, Date createDate);
}
