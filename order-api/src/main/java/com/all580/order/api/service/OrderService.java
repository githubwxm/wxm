package com.all580.order.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Date;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description: 订单服务
 * @date 2016/10/22 17:20
 */
public interface OrderService {
    /**
     * 根据第三方交易号获取收款企业ID
     * @param outTranId 第三方交易号
     * @return
     */
    Result<Integer> getPayeeEpIdByOutTransId(String outTranId);

    /**
     * 根据供应侧平台商ID查询平台商订单列表
     * @param coreEpId 供应侧平台商ID
     * @param startTime 下单开始时间
     * @param endTime 下单结束时间
     * @param orderStatus 订单状态
     * @param orderItemStatus 子订单状态
     * @param phone 联系人手机号
     * @param orderItemNumber 子订单流水编号
     * @param recordStart 记录开始
     * @param recordCount 记录数
     * @return
     */
    Result<PageRecord<Map>> selectPlatformOrderBySupplierCore(Integer coreEpId,
                                                            Date startTime,
                                                            Date endTime,
                                                            Integer orderStatus,
                                                            Integer orderItemStatus,
                                                            String phone,
                                                            Long orderItemNumber,
                                                            Integer recordStart,
                                                            Integer recordCount);
}
