package com.all580.notice.dao;

import com.all580.notice.entity.SmsAccountConf;

public interface SmsAccountConfMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SmsAccountConf record);

    int insertSelective(SmsAccountConf record);

    SmsAccountConf selectByPrimaryKey(Integer id);

    SmsAccountConf selectByEpId(Integer epId);

    int updateByPrimaryKeySelective(SmsAccountConf record);

    int updateByPrimaryKey(SmsAccountConf record);
}