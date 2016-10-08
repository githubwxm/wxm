package com.all580.ep.dao;

import com.all580.ep.api.entity.Ep;
import com.framework.common.Result;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface EpMapper {
    int create(Object obj);
    List<Ep> select (Map params);
    List<Ep>  getEp(Map params);
    List<Ep> all(Map params);
    List<List<?>>  validate(Map params);
    int update(Map params);
    int updateStatus(Map params);
}
