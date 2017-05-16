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
 * @date 17-5-15 下午4:21
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NoticeOrderConsumedRequestBody", propOrder = {
        "orderInfo"
})
@Setter
@Getter
public class NoticeOrderConsumedRequestBody extends RequestBody {
    @XmlElement(required = true)
    protected NoticeOrderConsumedRequestBody.OrderInfo orderInfo;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "partnerorderId",
            "orderQuantity",
            "useQuantity",
            "consumeInfo"
    })

    @Setter
    @Getter
    public static class OrderInfo {
        @XmlElement(required = true)
        protected String partnerorderId;
        @XmlElement
        protected int orderQuantity;
        @XmlElement
        protected int useQuantity;
        @XmlElement
        protected String consumeInfo;
    }
}
