package com.all580.payment.service;

import com.all580.order.api.service.OrderService;
import com.all580.order.api.service.PaymentCallbackService;
import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.all580.payment.dao.EpPaymentConfMapper;
import com.all580.payment.entity.EpPaymentConf;
import com.all580.payment.thirdpay.ali.service.AliPayService;
import com.all580.payment.thirdpay.wx.model.*;
import com.all580.payment.thirdpay.wx.service.WxPayService;
import com.all580.payment.thirdpay.wx.util.ConstantUtil;
import com.framework.common.Result;
import com.framework.common.event.MnsEvent;
import com.framework.common.event.MnsEventManager;
import com.framework.common.mns.TopicPushManager;
import com.framework.common.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
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
    private TopicPushManager topicPushManager;
    @Value("${mns.topic}")
    private String topicName;
    @Autowired
    private EpPaymentConfMapper epPaymentConfMapper;
    @Autowired
    private WxPayService wxPayService; // 本类中不要直接使用，应该用getWxPayService()
    @Autowired
    private AliPayService aliPayService; // 本类中不要直接使用，应该用getAliPayService()
    @Autowired
    private PaymentCallbackService paymentCallbackService;
    @Autowired
    private OrderService orderService;

//    @Autowired
//    private WxProperties wxProperties;



    @Override
    public Result refundQuery(Long ordCode,int coreEpId,Integer payType) {
        if(PaymentConstant.PaymentType.WX_PAY.equals(payType)){
            RefundReq req = new RefundReq();
            req.setOut_trade_no(ordCode+"");
            RefundRsp  rsp=null;
            try {
                rsp=  getWxPayService(coreEpId, payType).request(ConstantUtil.REFUNDQUERY, req, RefundRsp.class, false, coreEpId);
            }catch (Exception e){
                e.printStackTrace();
            }
            Result result = new Result(true);
            result.put(rsp.getRefund_status_0());
            return result;
        }else{
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
    }

    @Override
    public Result<Integer> getPaidStatus(long ordCode, int coreEpId, Integer payType,String trade_no) {
        Result result = new Result();
        String msg = "";
        Integer code=-1;
        if (PaymentConstant.PaymentType.WX_PAY.equals(payType)) {
            UnifiedOrderReq req = new UnifiedOrderReq();
            req.setOut_trade_no(ordCode+"");
            try {
                UnifiedOrderRsp rsp = getWxPayService(coreEpId, payType).request(ConstantUtil.ORDERQUERY, req, UnifiedOrderRsp.class, false, coreEpId);
                if ("SUCCESS".equalsIgnoreCase(rsp.getReturn_code()) && "SUCCESS".equalsIgnoreCase(rsp.getResult_code())) {
                    switch (rsp.getTrade_state()) {
                        case "SUCCESS":
                            StringBuffer sb = new StringBuffer(16);
                            Field[] fields = rsp.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                if (null != field.get(rsp)) {
                                    sb.append(field.getName() + "=" + field.get(rsp) + ";");
                                }
                            }
                            msg = "订单支付成功";
                            code=PaymentConstant.ThirdPayStatus.SUCCESS;
                            break;
                        case "NOTPAY":
                            msg = "订单未支付";
                            code=PaymentConstant.ThirdPayStatus.NOTPAY;
                            break;
                        case "USERPAYING":
                            msg = "订单正在支付中";
                            code=PaymentConstant.ThirdPayStatus.USERPAYING;
                            break;
                        case "PAYERROR":
                            msg = "订单支付失败";
                            code=PaymentConstant.ThirdPayStatus.PAYERROR;
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.setError(e.getMessage());
            }
        } else if (PaymentConstant.PaymentType.ALI_PAY.equals(payType)) {
//             QueryReq queryReq = new QueryReq();
//             queryReq.setOrderId(String.valueOf(order.getId()));
//             queryReq.setTrade_no(order.getTransaction_id());
            Map<String, String> paymentQuery = getAliPayService(coreEpId, payType).paymentQuery(ordCode, coreEpId,trade_no);
            if (paymentQuery.get("is_success") != null && paymentQuery.get("is_success").equals("T")) {
                switch (paymentQuery.get("trade_status")) {
                    case "WAIT_BUYER_PAY":
                        msg = "订单未支付";
                        code=PaymentConstant.ThirdPayStatus.NOTPAY;
                        break;
                    case "TRADE_CLOSED":
                        msg = "订单未支付,已关闭";
                        code=PaymentConstant.ThirdPayStatus.NOTPAY;
                        break;
                    case "TRADE_SUCCESS":
                    case "TRADE_FINISHED":
                        msg = "订单支付成功";
                        code=PaymentConstant.ThirdPayStatus.SUCCESS;
                        break;
                    case "TRADE_REFUSE":
                    case "TRADE_CANCEL":
                        msg = "订单支付取消";
                        code=PaymentConstant.ThirdPayStatus.NOTPAY;
                        break;
                }
            }
        } else {
                throw new RuntimeException("不支持的支付类型:" + payType);
        }
            result.put(code);
            result.setSuccess();
            return result;
    }

    @Override
    public Result<String> reqPay(long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        Result<String> result = new Result<>();

        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            try {
                String codeUrl = getWxPayService(coreEpId, payType).reqPay(ordCode, coreEpId, params);
                logger.info(codeUrl);
                result.setSuccess();
                result.put(codeUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String html = getAliPayService(coreEpId, payType).reqPay(ordCode, coreEpId, params);
            logger.info(html);
            result.put(html);
            result.setSuccess();
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return result;
    }

    /**
     * 发布余额变更事件
     */
    private void fireBalanceChangedEvent(Integer coreEpId,Object ordCode,Integer money,Integer refType ) {
        Map<String,Object> map = new HashMap<>();
        try{
            map.put("ref_id",ordCode);
            map.put("core_ep_id",coreEpId);
            map.put("money",money);
            map.put("ref_type",refType );//PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER
            logger.info("第三方金额变更事件----->开始");
            String tag = "core";
            //topicPushManager.asyncFireEvent(topicName, tag, PaymentConstant.EVENT_NAME_FUND_CHANGE, map);
            MnsEventManager.addEvent("FUND_CHANGE", map);
            logger.info("第三方金额变更事件----->成功");
        }catch (Exception e){
            logger.error("第三方金额变更事件 异常{}  ",map.toString());
        }
    }
    /* 获取aliPayService前，初始化平台商的账号配置信息 */
    private AliPayService getAliPayService(int coreEpId, int payType) {
        if (!aliPayService.isPropertiesInit(coreEpId)) {
            String confData = getConf(coreEpId, payType).getConf_data();
            aliPayService.initProperties(coreEpId, confData);
        }
        return aliPayService;
    }

    /* wxPayService，初始化平台商的账号配置信息 */
    private WxPayService getWxPayService(int coreEpId, int payType) {
        if (!wxPayService.isPropertiesInit(coreEpId)) {
            EpPaymentConf epPaymentConf = getConf(coreEpId, payType);
            wxPayService.initProperties(coreEpId, epPaymentConf.getConf_data(), epPaymentConf.getCert_p12());
        }
        return wxPayService;
    }

    private EpPaymentConf getConf(int coreEpId, int payType) {
        EpPaymentConf epPaymentConf = epPaymentConfMapper.getByEpIdAndType(coreEpId, payType);
        Assert.notNull(epPaymentConf, MessageFormat.format("没有找到支付方式配置：coreEpId={0}|payType={1}",
                coreEpId, payType));
        return epPaymentConf;
    }

    @Override
    @MnsEvent
    public Result<String> reqRefund(final long ordCode, int coreEpId, int payType, Map<String, Object> params) {
        Result<String> result = new Result<>();
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            // 微信退款，直接由后台发起，同步返回结果
            try {
                RefundRsp refundRsp = getWxPayService(coreEpId, payType).reqRefund(ordCode, params, coreEpId);
                logger.info("微信退款结果：" + refundRsp.getResult_code());
                result.setSuccess();
                logger.info("微信退款参数：" + params.toString());
                final String serialNum = String.valueOf(params.get("serialNum"));
                Integer money=CommonUtil.objectParseInteger(params.get("totalFee"));
                money=money==null?0:money;
                fireBalanceChangedEvent(coreEpId,serialNum,-money,PaymentConstant.BalanceChangeType.THIRD_QUIT_FOR_ORDER);
                result.put(refundRsp.getTransaction_id());
                // TODO panyi 异步回调订单-> 记录任务

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        logger.info("微信退款->回调订单开始。。。");
                        paymentCallbackService.refundCallback(ordCode, serialNum, null, true);
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            // 支付宝退款是构造form表单，由前台进行提交；退款结果异步回调
            String html = getAliPayService(coreEpId, payType).reqRefund(params, coreEpId);
            logger.info(html);
            result.put(html);
            result.setSuccess();
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return result;
    }

    @Override
    @MnsEvent
    public Result<Map<String, String>> payCallback(String ordId, String trade_no, Map<String, String> params, int payType) {
        // 根据企业获取配置信息
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            String attach = params.get("attach");
            int coreEpId = Integer.parseInt(attach);
            Result<Map<String, String>> payCallback = getWxPayService(coreEpId, payType).payCallback(params, coreEpId);
            if (payCallback.isSuccess()) {
                // 支付成功事件，参数:订单号，支付金额，流水号，收款平台商企业ID
                Result result = paymentCallbackService.payCallback(Long.valueOf(ordId), ordId, trade_no);

                fireBalanceChangedEvent(coreEpId,ordId, CommonUtil.objectParseInteger(params.get("total_fee")),PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER);
                if (result.isFault()) {
                    logger.error("微信支付回调.回调订单失败");
                }
            }
            return payCallback;
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String extraStr = params.get("extra_common_param");
            int coreEpId = Integer.parseInt(extraStr);
            Result<Map<String, String>> result = getAliPayService(coreEpId, payType).payCallback(params, coreEpId);
            if (result.isSuccess()) {
                Double m = Double.parseDouble(CommonUtil.objectParseString(params.get("total_fee")))*100;
                fireBalanceChangedEvent(coreEpId,ordId,m.intValue(),PaymentConstant.BalanceChangeType.THIRD_PAY_FOR_ORDER);
                Result callResult = paymentCallbackService.payCallback(Long.valueOf(ordId), ordId, trade_no);
                if (callResult.isFault()) {
                    logger.error("支付宝支付回调.回调订单失败");
                }
            }
            return result;
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
    }

    @Override
    @MnsEvent
    public Result refundCallback(Map<String, String> params, int payType) {
        Result rst = new Result();
        // 根据企业获取配置信息
        if (PaymentConstant.PaymentType.WX_PAY == payType) {
            throw new RuntimeException("微信没有退款回调");
        } else if (PaymentConstant.PaymentType.ALI_PAY == payType) {
            String resultDetails = params.get("result_details");
            logger.info("支付宝退款返回：result_details=" + resultDetails);
            String[] split = StringUtils.split(resultDetails, '^');
            String outTransId = split[0]; // 第三方交易号-支付宝的交易号
            Result<Integer> result = orderService.getPayeeEpIdByOutTransId(outTransId);
            Integer coreEpId = result.get();
            Assert.notNull(coreEpId, "通过外部交易号没有获取到平台商ID：outTransId=" + outTransId);
            // 验证调用结果是否成功
            boolean isSuccess = getAliPayService(coreEpId, payType).refundCallback(params, coreEpId);

            String batchNo = params.get("batch_no");
            String serialNum = batchNo.substring(8);
            Result result1 = paymentCallbackService.refundCallback(null, serialNum, outTransId, isSuccess);
            if (result1.isSuccess()) {
                Double m = Double.parseDouble(CommonUtil.objectParseString(split[1]))*100;
                fireBalanceChangedEvent(coreEpId,serialNum,-m.intValue() ,PaymentConstant.BalanceChangeType.THIRD_QUIT_FOR_ORDER);
                rst.setSuccess();
            } else {
                // ....................
                logger.error("支付宝退款回调订单->失败：" + result1.getError());
            }
        } else {
            throw new RuntimeException("不支持的支付类型:" + payType);
        }
        return rst;
    }

}
