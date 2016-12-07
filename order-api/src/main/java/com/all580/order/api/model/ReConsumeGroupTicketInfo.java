package com.all580.order.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 反核销回调信息
 * @date 2016/10/15 9:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReConsumeGroupTicketInfo implements Serializable {
    private static final long serialVersionUID = -7055510137379795922L;
    /** 验票流水 */
    private String validateSn;

    /** 张数 */
    private int quantity;

    /** 反核销流水 */
    private String reValidateSn;
}
