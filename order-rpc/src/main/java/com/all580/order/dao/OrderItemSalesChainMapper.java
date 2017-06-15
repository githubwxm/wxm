package com.all580.order.dao;

import com.all580.order.entity.OrderItemSalesChain;

public interface OrderItemSalesChainMapper {
    /**
     *  根据主键删除数据库的记录,t_order_item_sales_chain
     *
     * @param id
     */
    int deleteByPrimaryKey(Integer id);

    /**
     *  新写入数据库记录,t_order_item_sales_chain
     *
     * @param record
     */
    int insert(OrderItemSalesChain record);

    /**
     *  动态字段,写入数据库记录,t_order_item_sales_chain
     *
     * @param record
     */
    int insertSelective(OrderItemSalesChain record);

    /**
     *  根据指定主键获取一条数据库记录,t_order_item_sales_chain
     *
     * @param id
     */
    OrderItemSalesChain selectByPrimaryKey(Integer id);

    /**
     *  动态字段,根据主键来更新符合条件的数据库记录,t_order_item_sales_chain
     *
     * @param record
     */
    int updateByPrimaryKeySelective(OrderItemSalesChain record);

    /**
     *  根据主键来更新符合条件的数据库记录,t_order_item_sales_chain
     *
     * @param record
     */
    int updateByPrimaryKey(OrderItemSalesChain record);
}