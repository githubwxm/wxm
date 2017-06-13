package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.SyncTaskView;

public interface SyncTaskViewMapper {
    /**
     *  根据主键删除数据库的记录,t_synctaskview
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_synctaskview
     *
     * @param record
     */
    int insert(SyncTaskView record);

    /**
     *  动态字段,写入数据库记录,t_synctaskview
     *
     * @param record
     */
    int insertSelective(SyncTaskView record);

    /**
     *  根据指定主键获取一条数据库记录,t_synctaskview
     *
     * @param id
     */
    SyncTaskView selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_synctaskview
     *
     * @param record
     */
    int updateByPrimaryKeySelective(SyncTaskView record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_synctaskview
     *
     * @param record
     */
    int updateByPrimaryKey(SyncTaskView record);
}