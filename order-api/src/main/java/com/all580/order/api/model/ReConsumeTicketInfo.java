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
public class ReConsumeTicketInfo implements Serializable {
    private static final long serialVersionUID = -314939197622111279L;
    /** 验票流水 */
    private String validateSn;

    /** 游客ID */
    private int visitorSeqId;

    /** 反核销流水 */
    private String reValidateSn;
}
