package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 出票信息
 * @date 2016/10/15 9:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendTicketInfo {
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
