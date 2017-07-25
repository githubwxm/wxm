package com.all580.notice.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SmsTmpl implements Serializable {
    /**
     *  模版ID,所属表字段为t_sms_tmpl.id
     */
    private Integer id;

    /**
     *  标题,所属表字段为t_sms_tmpl.name
     */
    private String name;

    /**
     *  内容（支持标签：{prodName}{num}{custName}等等）,所属表字段为t_sms_tmpl.content
     */
    private String content;

    /**
     *  状态（有效：1，无效：0）,所属表字段为t_sms_tmpl.status
     */
    private Boolean status;

    /**
     *  归属企业，为0时为通用模版，为1时为验证码发送模板，不允许进行关联操作,所属表字段为t_sms_tmpl.ep_id
     */
    private Integer ep_id;

    /**
     *  创建时间,所属表字段为t_sms_tmpl.create_time
     */
    private Date create_time;

    /**
     *  模版类别（宣传广告，产品订购，售后服务等）,所属表字段为t_sms_tmpl.sms_type
     */
    private Integer sms_type;

    /**
     *  更新执行时间,所属表字段为t_sms_tmpl.update_time
     */
    private Date update_time;

    /**
     *  是否允许关联企业，验证码发送模板不允许进行关联操作,所属表字段为t_sms_tmpl.allow_associate_ep
     */
    private Boolean allow_associate_ep;

    /**
     *  12000: 普通短信; 12001: 凭证短信,所属表字段为t_sms_tmpl.channel_type
     */
    private Integer channel_type;

    /**
     *  在第三方短信端口中配置的模板id,所属表字段为t_sms_tmpl.out_sms_tpl_id
     */
    private String out_sms_tpl_id;

    /**
     * 序列化ID,t_sms_tmpl
     */
    private static final long serialVersionUID = 1L;

}