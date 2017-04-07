package com.all580.order.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2017/4/6 0006.
 */
public interface SyncExceptionOrder {
    Result selectSyncOrder(Long sn);
    Result SyncOrderAll(Long sn);
    Result selectOrderException(Map<String,Object> map);
}
