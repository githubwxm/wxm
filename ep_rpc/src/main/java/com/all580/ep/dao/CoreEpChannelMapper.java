package com.all580.ep.dao;

import com.all580.ep.api.entity.EpChannelRep;
import com.framework.common.Result;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/28 0028.
 */
public interface CoreEpChannelMapper {
   int create(Map params);
   int update(Map params);
   List<EpChannelRep> select(Map params);
   int cancel(@Param("id") Integer id);
}
