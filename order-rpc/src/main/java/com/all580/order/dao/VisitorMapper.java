package com.all580.order.dao;

import com.all580.order.entity.Visitor;
import com.all580.order.entity.VisitorKey;

public interface VisitorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int deleteByPrimaryKey(VisitorKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insert(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int insertSelective(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    Visitor selectByPrimaryKey(VisitorKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKeySelective(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Tue Sep 27 17:38:35 CST 2016
     */
    int updateByPrimaryKey(Visitor record);
}