package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Visitor implements Serializable {
    /**
     *  ,所属表字段为t_visitor.id
     */
    private Integer id;

    /**
     *  关联ID:子订单ID,所属表字段为t_visitor.ref_id
     */
    private Integer ref_id;

    /**
     *  团队ID 0 散客,所属表字段为t_visitor.group_id
     */
    private Integer group_id;

    /**
     *  联系电话,所属表字段为t_visitor.phone
     */
    private String phone;

    /**
     *  联系人姓名,所属表字段为t_visitor.name
     */
    private String name;

    /**
     *  证件类型 370 身份证 371 其它,所属表字段为t_visitor.card_type
     */
    private Integer card_type;

    /**
     *  身份证号,所属表字段为t_visitor.sid
     */
    private String sid;

    /**
     *  性别 3000 未知 3001 男 3002 女,所属表字段为t_visitor.sex
     */
    private Integer sex;

    /**
     *  票数,所属表字段为t_visitor.quantity
     */
    private Integer quantity;

    /**
     *  退票数量,所属表字段为t_visitor.return_quantity
     */
    private Integer return_quantity;

    /**
     *  核销数量,所属表字段为t_visitor.use_quantity
     */
    private Integer use_quantity;

    /**
     *  ,所属表字段为t_visitor.update_time
     */
    private String update_time;

    /**
     * 序列化ID,t_visitor
     */
    private static final long serialVersionUID = 1L;

}