package com.all580.base.adapter.push.qunar.request;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author zhouxianjun(Gary)
 * @ClassName:
 * @Description:
 * @date 17-5-15 下午4:24
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoticeOrderRefundApproveResultRequestBody", propOrder = {
        "orderInfo"
})
@Setter
@Getter
public class NoticeOrderRefundApproveResultRequestBody extends RequestBody {
    @XmlElement(required = true)
    protected NoticeOrderRefundApproveResultRequestBody.OrderInfo orderInfo;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "partnerorderId",
            "refundSeq",
            "orderQuantity",
            "refundQuantity",
            "refundJudgeMark",
            "refundResult"
    })

    @Setter
    @Getter
    public static class OrderInfo {
        @XmlElement(required = true)
        protected String partnerorderId;
        @XmlElement(required = true)
        protected String refundSeq;
        @XmlElement(required = true)
        protected int orderQuantity;
        @XmlElement(required = true)
        protected int refundQuantity;
        @XmlElement(required = true)
        protected String refundJudgeMark;
        @XmlElement(required = true)
        protected String refundResult;
    }
}
