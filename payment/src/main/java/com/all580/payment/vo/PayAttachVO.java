package com.all580.payment.vo;

/**
 * 支付透传参数VO
 * @author panyi on 2016/10/20.
 * @since V0.0.1
 */
public class PayAttachVO {
    private String coreEpId;
    private String serialNum;

    public PayAttachVO() {
    }

    public PayAttachVO(String coreEpId, String serialNum) {
        this.coreEpId = coreEpId;
        this.serialNum = serialNum;
    }

    public String getCoreEpId() {
        return coreEpId;
    }

    public void setCoreEpId(String coreEpId) {
        this.coreEpId = coreEpId;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }
}
