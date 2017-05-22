package com.all580.voucherplatform.dao;

import com.all580.voucherplatform.entity.TicketSys;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TicketSysMapper {
    /**
     *  根据主键删除数据库的记录,t_ticketsys
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_ticketsys
     *
     * @param record
     */
    int insert(TicketSys record);

    /**
     *  动态字段,写入数据库记录,t_ticketsys
     *
     * @param record
     */
    int insertSelective(TicketSys record);

    /**
     *  根据指定主键获取一条数据库记录,t_ticketsys
     *
     * @param id
     */
    TicketSys selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_ticketsys
     *
     * @param record
     */
    int updateByPrimaryKeySelective(TicketSys record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_ticketsys
     *
     * @param record
     */
    int updateByPrimaryKey(TicketSys record);


    int getCount();

    List<Map> getList(@Param("record_start") int recordStart, @Param("record_count")  int recordCount);
}