package com.all580.payment.thirdpay.wx.model;

import lombok.Data;

import java.util.Date;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/3/23 16:44
 */
@Data
public class AccessTokenBean {
    private String token;
    private String ticket;
    private int expiresSecond;
    private Date expires;
    private Date last;
    private String appId;
}
