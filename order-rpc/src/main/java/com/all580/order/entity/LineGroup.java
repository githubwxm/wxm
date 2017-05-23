package com.all580.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LineGroup implements Serializable {
    /**
     *  ,所属表字段为t_line_group.id
     */
    private Integer id;

    /**
     *  团号,所属表字段为t_line_group.number
     */
    private String number;

    /**
     *  团队前缀,所属表字段为t_line_group.team_prefix
     */
    private String team_prefix;

    /**
     *  团队后缀,所属表字段为t_line_group.team_suffix
     */
    private String team_suffix;

    /**
     *  导游名称,所属表字段为t_line_group.guide_name
     */
    private String guide_name;

    /**
     *  导游手机号,所属表字段为t_line_group.guide_phone
     */
    private String guide_phone;

    /**
     *  导游身份证,所属表字段为t_line_group.guide_sid
     */
    private String guide_sid;

    /**
     *  导游证件,所属表字段为t_line_group.guide_card
     */
    private String guide_card;

    /**
     *  出发地,所属表字段为t_line_group.place_of_departure
     */
    private String place_of_departure;

    /**
     *  目的地,所属表字段为t_line_group.destination
     */
    private String destination;

    /**
     *  天,所属表字段为t_line_group.days
     */
    private Integer days;

    /**
     *  ,所属表字段为t_line_group.nights
     */
    private Integer nights;

    /**
     *  出团日期,所属表字段为t_line_group.booking_date
     */
    private String booking_date;

    /**
     *  平台商ID,所属表字段为t_line_group.core_ep_id
     */
    private Integer core_ep_id;

    /**
     *  企业ID,所属表字段为t_line_group.ep_id
     */
    private Integer ep_id;

    /**
     *  团队状态;390 未出团 391 已出团 392 已完成,所属表字段为t_line_group.status
     */
    private Integer status;

    /**
     *  ,所属表字段为t_line_group.create_time
     */
    private Date create_time;

    /**
     * 序列化ID,t_line_group
     */
    private static final long serialVersionUID = 1L;

}