package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/1/24 14:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundMoneyEventParam implements Serializable {
    private static final long serialVersionUID = 5750609567252494716L;
    private Integer orderId;
    private String serialNo;

    private boolean success;
}
