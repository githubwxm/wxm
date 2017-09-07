package com.all580.order.dao;

import com.all580.order.dto.PackageOrderDto;
import com.all580.order.entity.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    /**
     *  根据主键删除数据库的记录,t_order
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_order
     *
     * @param record
     */
    int insert(Order record);

    /**
     *  动态字段,写入数据库记录,t_order
     *
     * @param record
     */
    int insertSelective(Order record);

    /**
     *  根据指定主键获取一条数据库记录,t_order
     *
     * @param id
     */
    Order selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKeySelective(Order record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_order
     *
     * @param record
     */
    int updateByPrimaryKey(Order record);

    /**
     * 根据订单编号(流水)获取订单
     * @param sn 编号(流水)
     * @return
     */
    Order selectBySN(Long sn);

    /**
     * 查询未支付的订单
     * @param minute 未支付时间
     * @return
     */
    List<Order> selectNoPayForMinute(int minute);

    /**
     * 查询待审核的订单
     * @param minute 时间
     * @return
     */
    List<Order> selectAuditWaitForMinute(int minute);

    /**
     * 根据支付第三方交易号获取订单
     * @param sn 第三方交易号
     * @return
     */
    Order selectByThirdSn(String sn);

    /**
     * 根据退订流水查找订单
     * @param sn 退订流水
     * @return
     */
    Order selectByRefundSn(Long sn);

    /**
     * 根据外部订单号查询订单
     * @param epId
     * @param outerId
     * @return
     */
    Order selectByOuter(@Param("epId") Integer epId, @Param("outerId") String outerId);

    /**
     * 查询支付中的订单
     * @return
     */
    List<Order> selectPayingOrder(@Param("minute")Integer minute);

    int setStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("before") Integer before);

    List<Map<String,Object>> selectByNumberItem(Long number);

    /**
     * 日志记录的查询
     * @return
     */
    Map selectByLog(@Param("orderId") Integer orderId, @Param("itemId") Integer itemId);

    /**
     * 查询套票订单关联的所有元素订单
     * @param orders
     * @return
     */
    List<PackageOrderDto> selectPackageOrder(@Param("orders") List<? extends Order> orders);

    /**
     * 查询套票元素订单的上层订单
     * @param orderId
     * @return
     */
    Order selectPackageOrderById(@Param("orderId") Integer orderId);

    /**
     * 查询套票订单的所有元素订单
     * @param orderId
     * @return
     */
    List<Order> selectPackageItemOrderById(@Param("orderId") Integer orderId);
}