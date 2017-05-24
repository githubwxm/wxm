package com.all580.order.dao;

import com.all580.order.entity.Visitor;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface VisitorMapper {
    /**
     *  根据主键删除数据库的记录,t_visitor
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_visitor
     *
     * @param record
     */
    int insert(Visitor record);

    /**
     *  动态字段,写入数据库记录,t_visitor
     *
     * @param record
     */
    int insertSelective(Visitor record);

    /**
     *  根据指定主键获取一条数据库记录,t_visitor
     *
     * @param id
     */
    Visitor selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_visitor
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Visitor record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_visitor
     *
     * @param record
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

    /**
     * 根据主键查询
     * @param ids
     * @return
     */
    List<Visitor> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 修改
     * @param refId
     * @return
     */
    int modify(@Param("refId") Integer refId);

    /**
     * 根据身份证和团队ID查询游客
     * @param sid 身份证
     * @param groupId 团队ID
     * @return
     */
    Visitor selectBySidAndGroup(@Param("sid") String sid, @Param("groupId") Integer groupId);

    int useQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    int refundQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);
}