package com.all580.order.api.service;

import com.framework.common.Result;
import com.framework.common.vo.PageRecord;

import java.util.Date;
import java.util.List;
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

    /**
     * 根据供应商平台查询订单
     * @param coreEpId 供应侧平台商ID
     * @param saleCoreEpId 销售商平台ID
     * @param dateType 时间类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param orderStatus 订单状态
     * @param orderItemStatus 子订单状态
     * @param phone 手机号码
     * @param orderItemNum 子订单
     * @param self 是否自供
     * @param productSubNumber 子产品ID
     * @param recordStart 记录开始
     * @param recordCount 记录数
     * @return
     */
    Result<PageRecord<Map>> selectBySupplierPlatform(Integer coreEpId,
                                                     Integer saleCoreEpId,
                                                     Integer dateType,
                                                     Date startTime,
                                                     Date endTime,
                                                     Integer orderStatus,
                                                     Integer orderItemStatus,
                                                     String phone,
                                                     Long orderItemNum,
                                                     Boolean self,
                                                     Long productSubNumber,
                                                     Integer recordStart,
                                                     Integer recordCount);

    /**
     * 查询通道费账单
     * @param coreEpId 供应平台商ID null则所有
     * @param start 开始时间
     * @param end 结束时间
     * @param settled 是否已结清
     * @return
     */
    Result<List<Map>> selectChannelBill(Integer coreEpId, Date start, Date end, Boolean settled);

    /**
     * 查询通道费账单明细
     * @param coreEpId 供应平台商ID null则所有
     * @param saleCoreEpId 销售平台商ID
     * @param month 月份
     * @return
     */
    Result<List<Map>> selectChannelBillDetail(Integer coreEpId, Integer saleCoreEpId, Integer month);

    /**
     * 查询供应平台通道费账单
     * @param coreEpId 供应平台商ID null则所有
     * @param start 开始时间
     * @param end 结束时间
     * @param settled 是否已结清
     * @return
     */
    Result<List<Map>> selectChannelBillForSupplier(Integer coreEpId, Date start, Date end, Boolean settled);

    /**
     * 设置某月账单为已结算
     * @param params
     * @return
     */
    Result settledChannelBill(Map params);
}
