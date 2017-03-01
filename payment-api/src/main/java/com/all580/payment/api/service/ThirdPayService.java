package com.all580.payment.api.service;

import com.all580.payment.api.conf.PaymentConstant;
import com.framework.common.Result;

import java.util.Map;

/**
 * 第三方支付接口，支持：微信、支付宝
 *
 * @author panyi on 2016/10/8.
 * @since V0.0.1
 */
public interface ThirdPayService {
    /**
     * 请求支付
     *
     * @param ordCode  订单编号
     * @param coreEpId 平台商企业ID
     * @param payType  支付类型 @see PaymentConstant.PaymentType
     * @param params   {prodName:xx,totalFee:xx,serialNum:xx,prodId:xx}
     *                 prodName - String - 商品名称[最长127个字节]
     *                 totalFee - int - 支付总金额，单位分
     *                 serialNum - String - 流水号
     *                 prodId - int - 子产品ID
     * @return 第三方支付请求数据：1、微信：二维码地址；2、支付宝：请求支付宝的form表单html片段
     */
    Result<String> reqPay(long ordCode, int coreEpId, int payType, Map<String, Object> params);

    /**
     * 退款请求
     *
     * @param ordCode  订单编号
     * @param coreEpId 平台商企业ID    int totalFee, int refundFee, String serialNum,
     * @param payType  支付类型 @see PaymentConstant.PaymentType
     * @param params   {totalFee:xx,refundFee:xx,serialNum:xx,outTransId:xx}
     *                 totalFee - int - 支付总金额，单位分
     *                 refundFee - int - 退款金额，单位分
     *                 serialNum - String - 退款流水号（订退订单号）
     *                 outTransId - String - 第三方交易号
     * @return 支付宝：返回form表单html片段；微信：后台直接发起退款，返回成功失败
     */
    Result<String> reqRefund(long ordCode, int coreEpId, int payType, Map<String, Object> params);

    // 支付回调，发布回调订单模块的任务
    Result<Map<String,String>> payCallback(String ordId,String trade_no,Map<String, String> params,int payType);


    // 退款回调，发布回调订单模块的任务
    Result refundCallback(Map<String,String> params,int payType);

    /**
     * 获取支付状态
     *
     * @param ordCode 订单编号
     */
    Result<PaymentConstant.ThirdPayStatus> getPaidStatus(long ordCode, int coreEpId, Integer payType,String trade_no);

    /**
     *查询退款状态    微信才有
     * @param ordCode
     * @param coreEpId
     * @param payType
     * @return
     */
     Result<PaymentConstant.ThirdPayStatus> refundQuery(Long ordCode, int coreEpId, Integer payType);
}
