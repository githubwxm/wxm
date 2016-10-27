package com.all580.payment.thirdpay.wx.service;

import com.all580.payment.entity.EpPaymentConf;
import com.all580.payment.thirdpay.wx.client.ClientResponseHandler;
import com.all580.payment.thirdpay.wx.client.RequestHandler;
import com.all580.payment.thirdpay.wx.client.ResponseHandler;
import com.all580.payment.thirdpay.wx.client.TenpayHttpClient;
import com.all580.payment.thirdpay.wx.model.*;
import com.all580.payment.thirdpay.wx.util.ConstantUtil;
import com.all580.payment.thirdpay.wx.util.WXUtil;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.net.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author panyi on 2016/10/13.
 * @since V0.0.1
 */
@Service("wxPayService")
public class WxPayService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${base.url}")
    private String domain;
    @Value("${weixin.callback.url}")
    private String notifyUrl;


    @Autowired
    private WxProperties wxProperties;

    public String reqPay(long ordCode,int coreEpId, Map<String, Object> params, String confData) throws Exception {
        WxProperties wxProperties2 = JsonUtils.fromJson(confData, WxProperties.class);
        domain = "";
        notifyUrl = "http://core.py.ngrok.wendal.cn/api/callback/wx/payment";
        UnifiedOrderReq req = new UnifiedOrderReq();
        req.setAppid(wxProperties.getAPP_ID());
        req.setMch_id(wxProperties.getPARTNER());
        req.setNonce_str(WXUtil.getNonceStr());
        req.setDevice_info("");
        req.setTotal_fee(String.valueOf(params.get("totalFee")));
        //透传参数
        // PayAttachVO attachVO = new PayAttachVO("" + coreEpId, String.valueOf(params.get("serialNum")));
        req.setAttach("" + coreEpId);
        req.setTrade_type(ConstantUtil.NATIVE_TRADE_TYPE);
        req.setProduct_id(String.valueOf(params.get("prodId")));
        req.setSpbill_create_ip(IPUtils.getRealIp(true));
        req.setFee_type("CNY");
        req.setNotify_url(domain + notifyUrl);
        req.setBody(String.valueOf(params.get("prodName")));
        // req.setGoods_tag();
        req.setOut_trade_no(String.valueOf(ordCode));
        // req.setSign();

        UnifiedOrderRsp unifiedOrderRsp = this.request(ConstantUtil.UNIFIEDORDER, req, UnifiedOrderRsp.class, false);

        return unifiedOrderRsp.getCode_url();

    }

    // 申请退款
    public RefundRsp reqRefund(long ordCode, Map<String, Object> params, String confData) throws Exception {
        WxProperties wxProperties2 = JsonUtils.fromJson(confData, WxProperties.class);
        RefundReq req = new RefundReq();
        req.setTransaction_id("");
        req.setOut_trade_no(String.valueOf(ordCode));
        req.setOut_refund_no(String.valueOf(params.get("refundId")));
        req.setTotal_fee((Integer) params.get("totalFee"));
        req.setRefund_fee((Integer) params.get("refundFee"));
        req.setRefund_fee_type("CNY");
        req.setAppid(wxProperties.getAPP_ID());
        req.setMch_id(wxProperties.getPARTNER());
        req.setNonce_str(WXUtil.getNonceStr());
        req.setOp_user_id(wxProperties.getPARTNER());
        req.setDevice_info("");

        return this.request(ConstantUtil.REFUND, req, RefundRsp.class, true);
    }

    public Result<Map<String, String>> payCallback(Map<String, String> params, EpPaymentConf epPaymentConf) {
        Result<Map<String, String>> result = new Result<>();
        WxProperties wxProperties = JsonUtils.fromJson(epPaymentConf.getConfData(), WxProperties.class);
        ResponseHandler resHandler = new ResponseHandler(params);
        resHandler.setKey(wxProperties.getPARTNER_KEY());
        Map<String,String> rsp = new HashMap<>();
        // 判断签名
        if (resHandler.isTenpaySign()) {
            if (null != resHandler.getParameter(ConstantUtil.RETURN_CODE) && resHandler.
                    getParameter(ConstantUtil.RETURN_CODE).equals(ConstantUtil.SUCCESS)) {
                rsp.put("return_code","SUCCESS");
                rsp.put("return_msg", "OK");
                result.setSuccess();
            } else {
                rsp.put("return_code", "FAIL");
                result.setFail();
            }
        } else {
            rsp.put("return_code", "FAIL");
            result.setFail();
        }
        result.put(rsp);
        return result;
    }

    /**
     * @param url
     * @param req
     * @param cls
     * @param certBool 是否需要证书
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T> T request(String url, CommonsReq req, Class<T> cls, boolean certBool) throws Exception {
        // 创建请求对象
        RequestHandler queryReq = new RequestHandler(null, null);
        // 通信对象
        TenpayHttpClient httpClient = new TenpayHttpClient();
        // 应答对象
        ClientResponseHandler queryRes = new ClientResponseHandler();

        // 通过通知ID查询，确保通知来至财付通
        queryReq.init();
        queryReq.setKey(wxProperties.getPARTNER_KEY());
        queryReq.setGateUrl(url);

        Class clss = req.getClass();
        for (; clss != Object.class; clss = clss.getSuperclass()) {
            Field[] fields = clss.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (null != field.get(req)) {
                        queryReq.setParameter(field.getName(), field.get(req).toString());
                    }
                } catch (IllegalArgumentException e) {
                    logger.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        // 通信对象
//        httpClient.setTimeOut(5);
        // 设置请求内容
        httpClient.setReqContent(queryReq.getRequestURL());
        T entity = cls.newInstance();

        if (certBool) {
            httpClient.setCaInfo(new File(wxProperties.getRootca_pem()));
            httpClient.setCertInfo(new File(wxProperties.getApiclient_cert_p12()), wxProperties.getPARTNER());
        }

        // 后台调用
        if (httpClient.call()) {
            String returnResult = httpClient.getResContent();
            logger.info("微信返回结果：" + returnResult);
            queryRes.setContent(returnResult);
            SortedMap map = queryRes.getAllParameters();
            // 验证是否失败
            if (null != map.get(ConstantUtil.RETURN_CODE) && map.get(ConstantUtil.RETURN_CODE).equals(ConstantUtil.SUCCESS)) {
                if (null != map.get(ConstantUtil.RESULT_CODE) && map.get(ConstantUtil.RESULT_CODE).equals(ConstantUtil.SUCCESS)) {
                    for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) it.next();
                        Class clsss = entity.getClass();
                        for (; clsss != Object.class; clsss = clsss.getSuperclass()) {
                            Field[] fields = clsss.getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                try {
                                    if (null != entry && entry.getKey().equals(field.getName())) {
                                        if (field.getType().equals(Integer.class)) {
                                            field.set(entity, entry.getValue() != null ? Integer.valueOf(entry.getValue().toString()) : 0);
                                        } else {
                                            field.set(entity, entry.getValue());
                                        }
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                    logger.error(e.getMessage(), e);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    }
                } else {
                    if (null != map.get(ConstantUtil.ERR_CODE_DES) && !"".equals(map.get(ConstantUtil.ERR_CODE_DES))) {
                        throw new Exception(map.get(ConstantUtil.ERR_CODE_DES).toString());
                    } else {
                        throw new Exception("微信接口调用失败");
                    }
                }
            } else {
                if (null != map.get(ConstantUtil.RETURN_MSG) && !"".equals(map.get(ConstantUtil.RETURN_MSG))) {
                    throw new Exception(map.get(ConstantUtil.RETURN_MSG).toString());
                } else {
                    throw new Exception("微信接口调用失败");
                }
            }
        } else {
            logger.info("后台调用通信失败");
            logger.info("" + httpClient.getResponseCode());
            logger.info(httpClient.getErrInfo());
            throw new Exception("后台调用通信失败");
        }
        return entity;
    }
}
