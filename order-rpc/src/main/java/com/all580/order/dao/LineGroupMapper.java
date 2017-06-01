package com.all580.order.dao;

import com.all580.order.entity.LineGroup;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface LineGroupMapper {
    /**
     *  根据主键删除数据库的记录,t_line_group
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_line_group
     *
     * @param record
     */
    int insert(LineGroup record);

    /**
     *  动态字段,写入数据库记录,t_line_group
     *
     * @param record
     */
    int insertSelective(LineGroup record);

    /**
     *  根据指定主键获取一条数据库记录,t_line_group
     *
     * @param id
     */
    LineGroup selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_line_group
     *
     * @param record
     */
    int updateByPrimaryKeySelective(LineGroup record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_line_group
     *
     * @param record
     */
    int updateByPrimaryKey(LineGroup record);

    /**
     * 根据团号查询
     * @param number
     * @param epId
     * @param coreEpId
     * @return
     */
    LineGroup selectByNumber(@Param("number") String number, @Param("epId") Integer epId, @Param("coreEpId") Integer coreEpId);

    /**
     * 巡查统计总计录数
     * @param params
     * @return
     */
    int countLineGroup(Map params);

    /**
     * 分页查询线路团队列表
     * @param params
     * @return
     */
    List<Map> listGroup(Map params);

    /**
     * 根据团号查询线路团队详情
     * @param number
     * @param epId
     * @return
     */
    Map getLineGroupDetailByNumber(@Param("number") String number, @Param("ep_id") String epId);

    /**
     * 根据团号统计线路团队游客数
     * @param groupNumber
     * @param epId
     * @return
     */
    int countLineGroupVisitorByGroupNumber(@Param("groupNumber") String groupNumber, @Param("ep_id") String epId);

    /**
     * 根据团号分页查询线路游客信息
     * @param number
     * @param record_start
     * @param record_count
     * @return
     */
    List<Map> getLineOrderVisitorsByNumber(@Param("number") String number, @Param("ep_id") String epId, @Param("record_start") int record_start, @Param("record_count") int record_count);

    /**
     * 根据团号和子订单号查询子订单信息
     * @param epId
     * @param groupNumber
     * @param orderNumbers
     * @return
     */
    List<Map> getLineOrdersByNumbers(@Param("ep_id") String epId, @Param("groupNumber") String groupNumber, @Param("orderNumbers") Collection<?> orderNumbers);

    /**
     * 根据团号修改
     * @param lineGroup
     * @return
     */
    int updateByNumberSelective(LineGroup lineGroup);
}