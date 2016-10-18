package com.all580.payment.api.model;

import java.io.Serializable;

/**
 * @author Created by panyi on 2016/9/28.
 *         各种支付请求的返回结果类
 */
public class PaymentReqResult implements Serializable {
    private Boolean success;
    private String msg;
    private String content;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
