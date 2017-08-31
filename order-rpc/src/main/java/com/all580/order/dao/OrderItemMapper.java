package com.all580.order.dao;

import com.all580.order.dto.PackageOrderItemDto;
import com.all580.order.entity.Order;
import com.all580.order.entity.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderItemMapper {
    /**
     *  根据主键删除数据库的记录,t_order_item
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_order_item
     *
     * @param record
     */
    int insert(OrderItem record);

    /**
     *  动态字段,写入数据库记录,t_order_item
     *
     * @param record
     */
    int insertSelective(OrderItem record);

    /**
     *  根据指定主键获取一条数据库记录,t_order_item
     *
     * @param id
     */
    OrderItem selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_order_item
     *
     * @param record
     */
    int updateByPrimaryKeySelective(OrderItem record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_order_item
     *
     * @param record
     */
    int updateByPrimaryKey(OrderItem record);

    /**
     * 根据子产品和身份证获取该身份证在指定时间内次数
     * @param productSubCode 子产品CODE
     * @param sid 身份证
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    int countBySidAndProductForDate(@Param("productSubCode") Long productSubCode, @Param("sid") String sid,
                                    @Param("start") Date start, @Param("end") Date end);

    /**
     * 根据订单ID查询子订单
     * @param orderId 订单ID
     * @return
     */
    List<OrderItem> selectByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID设置子订单状态
     * @param orderId 订单ID
     * @param status 状态
     * @return
     */
    int setStatusByOrderId(@Param("orderId") Integer orderId, @Param("status") Integer status);

    /**
     * 根据子订单编号获取
     * @param sn 编号
     * @return
     */
    OrderItem selectBySN(@Param("sn") Long sn);

    /**
     * 根据订单ID获取商品名称
     * @param orderId 订单ID
     * @return
     */
    List<String> getProductNamesByOrderId(@Param("orderId") Integer orderId);

    /**
     * 根据订单ID获取商品CODE
     * @param orderId 订单ID
     * @return
     */
    List<Long> getProductIdsByOrderId(@Param("orderId") Integer orderId);

    /**
     * 查询平台商订单列表
     * @param coreEpId 供应侧平台商ID
     * @param startTime 下单开始时间
     * @param endTime   下单结束时间
     * @param orderStatus 订单状态
     * @param orderItemStatus 子订单状态
     * @param phone 联系人手机
     * @param orderItemNum 子订单号
     * @return
     */
    List<Map> selectPlatformBySupplierCoreEpId(@Param("coreEpId") Integer coreEpId,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime,
                                               @Param("orderStatus") Integer orderStatus,
                                               @Param("orderItemStatus") Integer orderItemStatus,
                                               @Param("phone") String phone,
                                               @Param("orderItemNumber") Long orderItemNum,
                                               @Param("record_start") Integer recordStart,
                                               @Param("record_count") Integer recordCount);

    int selectPlatformBySupplierCoreEpIdCount(@Param("coreEpId") Integer coreEpId,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime,
                                               @Param("orderStatus") Integer orderStatus,
                                               @Param("orderItemStatus") Integer orderItemStatus,
                                               @Param("phone") String phone,
                                               @Param("orderItemNumber") Long orderItemNum);

    List<Map> selectBySupplierPlatform(@Param("coreEpId") Integer coreEpId,
                                       @Param("saleCoreEpId") Integer saleCoreEpId,
                                       @Param("dateType") Integer dateType,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime,
                                       @Param("orderStatus") Integer orderStatus,
                                       @Param("orderItemStatus") Integer orderItemStatus,
                                       @Param("phone") String phone,
                                       @Param("orderItemNumber") Long orderItemNum,
                                       @Param("self") Boolean self,
                                       @Param("productSubNumber") Long productSubNumber,
                                       @Param("record_start") Integer recordStart,
                                       @Param("record_count") Integer recordCount);

    int selectBySupplierPlatformCount(@Param("coreEpId") Integer coreEpId,
                                      @Param("saleCoreEpId") Integer saleCoreEpId,
                                      @Param("dateType") Integer dateType,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime,
                                      @Param("orderStatus") Integer orderStatus,
                                      @Param("orderItemStatus") Integer orderItemStatus,
                                      @Param("phone") String phone,
                                      @Param("orderItemNumber") Long orderItemNum,
                                      @Param("self") Boolean self,
                                      @Param("productSubNumber") Long productSubNumber);

    int selectCountByGroup(@Param("groupId") Integer groupId);

    int useQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    int resendTicket(@Param("id") Integer id);

    int refundQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    /**
     * 根据凭证商户查询未核销的订单
     * @return
     */
    List<OrderItem> selectByNotConsumeAndEpMaId(@Param("epMaIds") List<Integer> epMaIds, @Param("current") Integer current);

    /**
     * 查询OTA订单信息
     * @param sn 流水号
     * @return
     */
    Map selectInfoForOtaBySn(@Param("sn") Long sn);

    /**
     * 获取套票元素子订单的上一层子订单
     * @param orderItem
     * @return
     */
    PackageOrderItemDto selectPackageOrderItem(OrderItem orderItem);

    /**
     * 获取套票子订单的下层所有元素订单的子订单
     * @param itemId
     * @return
     */
    List<OrderItem> selectOrderItemsForPackageOrder(@Param("itemId") Integer itemId);

    /**
     * 查询套票订单的下层订单的所有子订单
     * @param order
     * @return
     */
    List<OrderItem> selectOrderItemsForUpperPackageOrder(Order order);
}