package com.all580.payment.service;

import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.payment.dao.EpPaymentConfMapper;
import com.all580.payment.entity.EpPaymentConf;
import com.all580.payment.thirdpay.wx.service.WxPaymentService;
import com.framework.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private WxPaymentService wxPaymentService;


    @Override
    public Result<String> reqPay(long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        Result<String> result = new Result<>();
        EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
        Assert.notNull(epPaymentConf);
        String confData = epPaymentConf.getConfData();
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            try {
                String codeUrl = wxPaymentService.reqPay(ordCode, params, confData);
                logger.info(codeUrl);
                result.setSuccess();
                result.put(codeUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            // TODO
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return result;
    }

    @Override
    public Result<String> reqRefund(long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        return null;
    }

    @Override
    public Result payCallback(int payType) {
        return null;
    }

    @Override
    public Result refundCallback(int payType) {
        return null;
    }

    @Override
    public Result<byte[]> getQrCode(String ordCode) {
        return null;
    }

    @Override
    public Result getPaidStatus(Long ordCode) {
        return null;
    }
}
