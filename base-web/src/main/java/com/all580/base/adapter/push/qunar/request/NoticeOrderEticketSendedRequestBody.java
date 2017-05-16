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
 * @date 17-5-15 下午2:48
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoticeOrderEticketSendedRequestBody", propOrder = {
        "orderInfo"
})
@Setter
@Getter
public class NoticeOrderEticketSendedRequestBody extends RequestBody {
    @XmlElement(required = true)
    protected NoticeOrderEticketSendedRequestBody.OrderInfo orderInfo;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "partnerorderId",
            "eticketNo",
            "eticketSended"
    })

    @Setter
    @Getter
    public static class OrderInfo {
        @XmlElement(required = true)
        protected String partnerorderId;
        @XmlElement
        protected String eticketNo;
        @XmlElement(required = true)
        protected boolean eticketSended;
    }
}
