package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GroupMember implements Serializable {
    /**
     * 所属表字段为 t_group_member.id
     */
    private Integer id;

    /**
     * 团队ID
     * 所属表字段为 t_group_member.group_id
     */
    private Integer group_id;

    /**
     * 姓名
     * 所属表字段为 t_group_member.name
     */
    private String name;

    /**
     * 证件类型
     * 所属表字段为 t_group_member.card_type
     */
    private Integer card_type;

    /**
     * 证件号码
     * 所属表字段为 t_group_member.card
     */
    private String card;

    /**
     * 手机号
     * 所属表字段为 t_group_member.phone
     */
    private String phone;

    /**
     * 是否成人
     * 所属表字段为 t_group_member.adult
     */
    private Boolean adult;

    /**
     * 创建用户ID
     * 所属表字段为 t_group_member.create_user_id
     */
    private Integer create_user_id;

    /**
     * 创建用户名称
     * 所属表字段为 t_group_member.create_user_name
     */
    private String create_user_name;

    /**
     * 创建时间
     * 所属表字段为 t_group_member.create_time
     */
    private Date create_time;

    /**
     * 所属表字段为 t_group_member.update_time
     */
    private String update_time;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_group_member
     *
     * @mbggenerated Fri Dec 02 09:17:42 CST 2016
     */
    private static final long serialVersionUID = 1L;

}