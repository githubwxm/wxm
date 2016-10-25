package com.all580.payment.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.payment.dao.EpPaymentConfMapper;
import com.all580.payment.entity.EpPaymentConf;
import com.all580.payment.thirdpay.ali.service.AliPayService;
import com.all580.payment.thirdpay.wx.model.RefundRsp;
import com.all580.payment.thirdpay.wx.service.WxPayService;
import com.all580.payment.vo.PayAttachVO;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.sun.jdi.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 第三方支付实现类：微信；支付宝
 *
 * @author Created by panyi on 2016/9/28.
 */
@Service("thirdPayService")
public class ThirdPayServiceImpl implements ThirdPayService {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EpPaymentConfMapper epPaymentConfMapper;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Autowired
    private OrderService orderService;

    @Override
    public Result<String> reqPay(long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        Result<String> result = new Result<>();
        EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
        Assert.notNull(epPaymentConf);
        String confData = epPaymentConf.getConfData();
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            try {
                String codeUrl = wxPayService.reqPay(ordCode,coreEpId, params, confData);
                logger.info(codeUrl);
                result.setSuccess();
                result.put(codeUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String html = aliPayService.reqPay(ordCode,coreEpId, params, confData);
            logger.info(html);
            result.put(html);
            result.setSuccess();
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return result;
    }

    @Override
    public Result<String> reqRefund(long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        Result<String> result = new Result<>();
        EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
        Assert.notNull(epPaymentConf);
        String confData = epPaymentConf.getConfData();
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            // 微信退款，直接由后台发起，同步返回结果
            try {
                RefundRsp refundRsp = wxPayService.reqRefund(ordCode, params, confData);
                logger.info(refundRsp.getResult_code());
                result.setSuccess();
                result.put(refundRsp.getTransaction_id());
                // TODO panyi 异步回调订单
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            // 支付宝退款是构造form表单，由前台进行提交；退款结果异步回调
            String html = aliPayService.reqRefund(params, confData);
            logger.info(html);
            result.put(html);
            result.setSuccess();
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return result;
    }

    @Override
    public Result<Map<String,String>> payCallback(String ordId,String trade_no,Map<String, String> params,int payType) {
        // 根据企业获取配置信息
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            String attach = params.get("attach");
            PayAttachVO payAttachVO = JsonUtils.fromJson(attach, PayAttachVO.class);
            int coreEpId =Integer.parseInt(payAttachVO.getCoreEpId());
            EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
            wxPayService.payCallback(params,epPaymentConf);
            paymentCallbackService.payCallback(Long.valueOf(ordId),null,payAttachVO.getSerialNum());
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String extraStr = params.get("extra_common_param");
            PayAttachVO payAttachVO = JsonUtils.fromJson(extraStr, PayAttachVO.class);
            int coreEpId =Integer.parseInt(payAttachVO.getCoreEpId());
            EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
            aliPayService.payCallback(params, epPaymentConf);
            paymentCallbackService.payCallback(Long.valueOf(ordId),null,payAttachVO.getSerialNum());
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return null;
    }

    @Override
    public Result refundCallback(Map<String,String> params, int payType) {
        // 根据企业获取配置信息
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            throw new RuntimeException("微信没有退款回调");
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String resultDetails = params.get("result_details");
            logger.info("支付宝退款返回：result_details=" + resultDetails);
            String[] split = resultDetails.split("^");
            String outTransId = split[0]; // 第三方交易号
            Result<Integer> result = orderService.getPayeeEpIdByOutTransId(outTransId);
            EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(result.get(), payType);
            // 验证调用结果是否成功
            boolean isSuccess = aliPayService.refundCallback(params, epPaymentConf);

            String batchNo = params.get("batch_no");
            String serialNum = batchNo.substring(8);
            paymentCallbackService.refundCallback(null,serialNum,outTransId,isSuccess);
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return null;
    }

    @Override
    public Result getPaidStatus(Long ordCode) {
        return null;
    }

}
