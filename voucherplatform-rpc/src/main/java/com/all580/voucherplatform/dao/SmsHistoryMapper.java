package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.SmsHistory;

public interface SmsHistoryMapper {
    /**
     *  根据主键删除数据库的记录,t_smshistory
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_smshistory
     *
     * @param record
     */
    int insert(SmsHistory record);

    /**
     *  动态字段,写入数据库记录,t_smshistory
     *
     * @param record
     */
    int insertSelective(SmsHistory record);

    /**
     *  根据指定主键获取一条数据库记录,t_smshistory
     *
     * @param id
     */
    SmsHistory selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_smshistory
     *
     * @param record
     */
    int updateByPrimaryKeySelective(SmsHistory record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_smshistory
     *
     * @param record
     */
    int updateByPrimaryKey(SmsHistory record);
}