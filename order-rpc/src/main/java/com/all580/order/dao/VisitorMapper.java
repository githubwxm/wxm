package com.all580.order.dao;

import com.all580.order.entity.Visitor;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface VisitorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    int insert(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    int insertSelective(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    Visitor selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    int updateByPrimaryKeySelective(Visitor record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_visitor
     *
     * @mbggenerated Wed Nov 09 20:17:14 CST 2016
     */
    int updateByPrimaryKey(Visitor record);

    /**
     * 根据子产品和身份证获取该身份证在指定时间内订购张数
     * @param productSubCode 子产品CODE
     * @param sid 身份证
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    int quantityBySidAndProductForDate(@Param("productSubCode") Long productSubCode, @Param("sid") String sid,
                                       @Param("start") Date start, @Param("end") Date end);

    /**
     * 根据子订单详情ID获取游客信息
     * @param detailId 子订单详情ID
     * @return
     */
    List<Visitor> selectByOrderDetailId(@Param("detailId") Integer detailId);

    /**
     * 根据发码信息获取游客信息
     * @param detailId 子订单详情ID
     * @param sid 身份证
     * @param phone 手机号码
     * @return
     */
    Visitor selectByMa(@Param("detailId") Integer detailId, @Param("sid") String sid, @Param("phone") String phone);

    /**
     * 根据订单ID获取所有游客信息
     * @param orderId 订单ID
     * @return
     */
    List<Visitor> selectByOrder(@Param("orderId") Integer orderId);

    /**
     * 根据子订单获取游客信息
     * @param itemId 子订单ID
     * @return
     */
    List<Visitor> selectByOrderItem(@Param("itemId") Integer itemId);
}