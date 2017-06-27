package com.all580.payment.api.service;

import com.framework.common.Result;

import java.util.List;
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
     * ******************************* 微信 **************************************
     *
     * ***************************************************************************
     * @param data {"coreEpId":xxx,"paymentType":7112,"confData":"...","certP12":微信证书文件base64，支付宝传空}
     *             confData:
     *               wx:{"appId":"wx8de3fa23129fcb3f","mchId":"1228533602","mchKey":"ECall58020150318WeiXinPayServers"}
     *               ali:{"partner":"2088111108611396","key":"5k7pyukjkmtut27t0h2vae4g9xr3i83r","sellerEmail":"admin@all580.com"}
     *             paymentType 取值来自于-> PaymentConstant.PaymentType
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result create(Map<String, Object> data);

    /**
     * 删除收款方式配置
     * @param data
     * @return
     */
    Result delete (Map<String, Object> data);
    /**
     * 修改收款方式配置
     *
     * @param data {"coreEpId":xxx,"paymentType":7112,"confData":"...","certP12":微信证书文件base64，支付宝传空}
     *             confData:
     *               wx:{"appId":"wx8de3fa23129fcb3f","mchId":"1228533602","mchKey":"ECall58020150318WeiXinPayServers"}
     *               ali:{"partner":"2088111108611396","key":"5k7pyukjkmtut27t0h2vae4g9xr3i83r","sellerEmail":"admin@all580.com"}
     *             paymentType 取值来自于-> PaymentConstant.PaymentType
     *             status 取值来自于-> PaymentConstant.Status
     * @return 成功，返回{success:true}；失败，返回{success:false,error:错误信息}
     */
    Result update(Map<String, Object> data);

    /**
     * 通过企业ID获取收款方式配置信息
     *
     * @param epId 企业ID
     * @return {"core_ep_id":xxx,"payment_type":7112,"conf_data":"...","status":1}
     */
    Result<List<Map<String, String>>> listByEpId(Integer epId);


    /**
     * 通过id查询收款方式配置信息
     * @param id
     * @return
     */
    Result selectByPrimaryKey(Integer id);

    /**
     * 心跳接口
     * @return
     */
    Result<?> heartbeat();
}
