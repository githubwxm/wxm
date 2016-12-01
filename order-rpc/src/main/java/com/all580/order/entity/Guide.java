package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Guide implements Serializable {
    /**
     * 主键
     * 所属表字段为 t_guide.id
     */
    private Integer id;

    /**
     * 导游姓名
     * 所属表字段为 t_guide.name
     */
    private String name;

    /**
     * 导游手机
     * 所属表字段为 t_guide.phone
     */
    private String phone;

    /**
     * 身份证
     * 所属表字段为 t_guide.sid
     */
    private String sid;

    /**
     * 导游证
     * 所属表字段为 t_guide.card
     */
    private String card;

    /**
     * 创建人ID
     * 所属表字段为 t_guide.create_user_id
     */
    private Integer create_user_id;

    /**
     * 创建人名称
     * 所属表字段为 t_guide.create_user_name
     */
    private String create_user_name;

    /**
     * 企业ID
     * 所属表字段为 t_guide.ep_id
     */
    private Integer ep_id;

    /**
     * 平台商ID
     * 所属表字段为 t_guide.core_ep_id
     */
    private Integer core_ep_id;

    /**
     * 创建时间
     * 所属表字段为 t_guide.create_time
     */
    private Date create_time;

    /**
     * 所属表字段为 t_guide.update_time
     */
    private String update_time;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_guide
     *
     * @mbggenerated Thu Dec 01 11:25:59 CST 2016
     */
    private static final long serialVersionUID = 1L;

}