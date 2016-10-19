package com.all580.notice.dao;

import com.all580.notice.entity.SmsTmpl;
import org.apache.ibatis.annotations.Param;

public interface SmsTmplMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsTmpl record);

    int insertSelective(SmsTmpl record);

    SmsTmpl selectByPrimaryKey(Integer id);

    SmsTmpl selectByEpIdAndType(@Param("epId") Integer epId, @Param("smsType") Integer smsType);

    int updateByPrimaryKeySelective(SmsTmpl record);

    int updateByPrimaryKey(SmsTmpl record);
}