package com.all580.payment.api.service;

import com.framework.common.Result;

import java.util.Map;

/**
 * 企业收款方式配置服务接口
 *
 * @author panyi on 2016/10/11
 * @since V0.0.1
 */
public interface EpPaymentConfService {

    /**
     * 创建支付方式配置
     *
     * @param data {"coreEpId":xxx,"paymentType":7112,"confData":"..."}
     *             paymentType 取值来自于-> PaymentConstant.PaymentType
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result create(Map<String, Object> data);

    /**
     * 修改收款方式配置
     *
     * @param data {"coreEpId":xxx,"paymentType":7112,"confData":"...","status":1}
     *             paymentType 取值来自于-> PaymentConstant.PaymentType
     *             status 取值来自于-> PaymentConstant.Status
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result update(Map<String, Object> data);

    /**
     * 通过企业ID获取收款方式配置信息
     *
     * @param epId 企业ID
     * @return {"coreEpId":xxx,"paymentType":7112,"confData":"...","status":1}
     */
    Result<Map<String, Object>> findById(Integer epId);

}
