package com.all580.order.dto;

import lombok.Data;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/2/7 10:50
 */
@Data
public class CreateOrder {
    /** 下单 企业ID*/
    private int epId;
    /** 下单企业名称 */
    private String epName;
    /** 下单企业平台商ID */
    private int coreEpId;
    /** 来源：代收 / 非代收 */
    private int from;
    /** 备注 */
    private String remark;
    /** 操作用户ID */
    private int operatorId;
    /** 操作用户姓名 */
    private String operatorName;
    /** 外部信息 */
    private String outer;
}
