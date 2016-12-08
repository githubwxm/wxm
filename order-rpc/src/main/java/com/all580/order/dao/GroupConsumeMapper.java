package com.all580.order.dao;

import com.all580.order.entity.GroupConsume;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupConsumeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    int insert(GroupConsume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    int insertSelective(GroupConsume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    GroupConsume selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    int updateByPrimaryKeySelective(GroupConsume record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_group_consume
     *
     * @mbggenerated Thu Dec 08 17:14:02 CST 2016
     */
    int updateByPrimaryKey(GroupConsume record);

    /**
     * 根据验票流水查询
     * @param sn
     * @return
     */
    List<GroupConsume> selectBySn(@Param("sn") String sn);
}