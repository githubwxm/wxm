package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Visitor implements Serializable {
    /**
     * 所属表字段为 t_visitor.id
     */
    private Integer id;

    /**
     * 关联ID:子订单详情ID
     * 所属表字段为 t_visitor.ref_id
     */
    private Integer refId;

    /**
     * 联系电话
     * 所属表字段为 t_visitor.phone
     */
    private String phone;

    /**
     * 联系人姓名
     * 所属表字段为 t_visitor.name
     */
    private String name;

    /**
     * 身份证号
     * 所属表字段为 t_visitor.sid
     */
    private String sid;

    /**
     * 票数
     * 所属表字段为 t_visitor.quantity
     */
    private Integer quantity;

    /**
     * 申请退票数量
     * 所属表字段为 t_visitor.pre_return
     */
    private Integer preReturn;

    /**
     * 退票数量
     * 所属表字段为 t_visitor.return_quantity
     */
    private Integer returnQuantity;

    /**
     * 核销数量
     * 所属表字段为 t_visitor.use_quantity
     */
    private Integer useQuantity;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_visitor
     *
     * @mbggenerated Fri Oct 14 14:49:08 CST 2016
     */
    private static final long serialVersionUID = 1L;

}