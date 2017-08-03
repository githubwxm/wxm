package com.all580.notice.dao;

import com.all580.notice.entity.SmsAccountConf;
import org.apache.ibatis.annotations.Param;

public interface SmsAccountConfMapper {
    /**
     *  根据主键删除数据库的记录,t_sms_account_conf
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_sms_account_conf
     *
     * @param record
     */
    int insert(SmsAccountConf record);

    /**
     *  动态字段,写入数据库记录,t_sms_account_conf
     *
     * @param record
     */
    int insertSelective(SmsAccountConf record);

    /**
     *  根据指定主键获取一条数据库记录,t_sms_account_conf
     *
     * @param id
     */
    SmsAccountConf selectByPrimaryKey(Integer id);

    SmsAccountConf selectByEpId(@Param("epId") Integer epId);

    int updateByPrimaryKeySelective(SmsAccountConf record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_sms_account_conf
     *
     * @param record
     */
    int updateByPrimaryKey(SmsAccountConf record);
}