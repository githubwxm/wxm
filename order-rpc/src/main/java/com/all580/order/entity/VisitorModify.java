package com.all580.order.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
public class VisitorModify implements Serializable {
    /**
     *  ,所属表字段为t_visitor_modify.id
     */
    private Integer id;

    /**
     *  游客ID,所属表字段为t_visitor_modify.visitor_id
     */
    private Integer visitor_id;

    /**
     *  游客名称,所属表字段为t_visitor_modify.visitor_name
     */
    private String visitor_name;

    /**
     *  游客手机,所属表字段为t_visitor_modify.visitor_phone
     */
    private String visitor_phone;

    /**
     *  游客证件,所属表字段为t_visitor_modify.visitor_sid
     */
    private String visitor_sid;

    /**
     *  游客证件类型,所属表字段为t_visitor_modify.visitor_card_type
     */
    private Integer visitor_card_type;

    /**
     *  子订单ID,所属表字段为t_visitor_modify.item_id
     */
    private Integer item_id;

    /**
     *  0 未修改 1 已修改,所属表字段为t_visitor_modify.status
     */
    private Boolean status;

    /**
     *  ,所属表字段为t_visitor_modify.create_time
     */
    private Date create_time;

    /**
     * 序列化ID,t_visitor_modify
     */
    private static final long serialVersionUID = 1L;

}