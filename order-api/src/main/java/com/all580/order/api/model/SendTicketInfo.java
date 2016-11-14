package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票信息
 * @date 2016/10/15 9:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTicketInfo implements Serializable {
    private static final long serialVersionUID = 1712445078030149125L;
    /** 游客ID */
    private int visitorSeqId;
    /** 二维码图像链接 */
    private String imgUrl;

    /** 电子凭证号 */
    private String voucherNumber;

    /** 票ID */
    private String ticketId;

    /** 凭证产品ID */
    private String maProductId;

    /** 身份证 */
    private String sid;

    /** 手机号码 */
    private String phone;
}
