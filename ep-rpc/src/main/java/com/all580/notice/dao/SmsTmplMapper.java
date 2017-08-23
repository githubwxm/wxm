package com.all580.notice.dao;

import com.all580.notice.entity.SmsTmpl;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsTmplMapper {
    /**
     *  根据主键删除数据库的记录,t_sms_tmpl
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_sms_tmpl
     *
     * @param record
     */
    int insert(SmsTmpl record);

    /**
     *  动态字段,写入数据库记录,t_sms_tmpl
     *
     * @param record
     */
    int insertSelective(SmsTmpl record);

    /**
     *  根据指定主键获取一条数据库记录,t_sms_tmpl
     *
     * @param id
     */
    SmsTmpl selectByPrimaryKey(Integer id);

    SmsTmpl selectByEpIdAndType(@Param("ep_id") Integer epId, @Param("sms_type") Integer smsType);

    int updateByPrimaryKeySelective(SmsTmpl record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_sms_tmpl
     *
     * @param record
     */
    int updateByPrimaryKey(SmsTmpl record);

    List<Map> selectByEp(@Param("epId") Integer epId, @Param("type") Integer type);
}