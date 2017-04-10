package com.all580.ep.dao;

import com.all580.ep.entity.EpPush;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EpPushMapper {
    /**
     *  根据主键删除数据库的记录,t_ep_push
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_ep_push
     *
     * @param record
     */
    int insert(EpPush record);

    /**
     *  动态字段,写入数据库记录,t_ep_push
     *
     * @param record
     */
    int insertSelective(EpPush record);

    /**
     *  根据指定主键获取一条数据库记录,t_ep_push
     *
     * @param id
     */
    EpPush selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_ep_push
     *
     * @param record
     */
    int updateByPrimaryKeySelective(EpPush record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_ep_push
     *
     * @param record
     */
    int updateByPrimaryKey(EpPush record);

    List<EpPush> selectByEpId(@Param("epId") String epId);
}