package com.all580.role.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * Created by wxming on 2016/11/10 0010.
 */
public interface IntfService {
    /**
     * @param params  name,auth   --  ,status   func_id   必填 ，菜单的id
     * @return
     */
    Result insertInft(Map<String,Object> params);
    Result deleteInft(int id);

    Result selectFuncId(int id);


}
