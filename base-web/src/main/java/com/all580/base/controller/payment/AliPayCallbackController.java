package com.all580.base.controller.payment;

import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 支付宝回调网关
 *
 * @author panyi on 2016/10/20.
 * @since V0.0.1
 */
@Controller
@RequestMapping("no_auth_api/callback/ali")
public class AliPayCallbackController extends BaseController {
    @Resource
    private ThirdPayService thirdPayService;

    /**
     * 退款回调
     *
     * @throws IOException
     */
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public void refund(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        logger.info("支付宝退款回调->开始");
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //批次号

//            String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");
            //批量退款数据中转账成功的笔数

//            String success_num = new String(request.getParameter("success_num").getBytes("ISO-8859-1"), "UTF-8");

            //批量退款数据中的详细信息
//            String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"), "UTF-8");
//            String sign = new String(request.getParameter("sign").getBytes("ISO-8859-1"), "UTF-8");
            logger.info("回调参数：" + JsonUtils.toJson(params));

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

            Result result = thirdPayService.refundCallback(params, PaymentConstant.PaymentType.ALI_PAY);
            if (result.isSuccess()) {//验证成功
                logger.info("支付宝退款回调->成功");
                rsp.getWriter().println("success");    //请不要修改或删除
            } else {//验证失败
                logger.error("支付宝退款回调->失败:" + result.getError());
                rsp.getWriter().println("fail");
            }
        } catch (Exception e) {
            logger.error("支付宝退款回调->失败:" + e.getMessage(), e);
            rsp.getWriter().println("fail");
        }
    }

    /**
     * 支付回调
     *
     * @throws IOException
     */
    @RequestMapping(value = "/payment")
    public void payment(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        logger.info("支付宝支付回调->开始");
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号

            String ordId = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            String transId = new String(request.getParameter("extra_common_param").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            logger.info("支付宝支付回调单号：" + ordId + "|支付宝交易号：" + trade_no + "|交易状态：" + trade_status);

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            Result result = thirdPayService.payCallback(ordId, trade_no, params, PaymentConstant.PaymentType.ALI_PAY);
            if (result.isSuccess()) {//验证成功
                logger.info("支付宝支付回调->成功");
                rsp.getWriter().println("success");    //请不要修改或删除

            } else {//验证失败
                logger.error("支付宝支付回调->失败：" + result.getError());
                rsp.getWriter().println("fail");
            }
        } catch (Exception e) {
            logger.error("支付宝支付回调->失败," + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /* 测试方法，不要调用 */
    @RequestMapping(value = "/reqPayTest")
    public void reqPayTest(HttpServletResponse rsp) throws Exception {
        System.out.println("---------------------------->   /reqPay");
        long ordCode = 1111111120;
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.ALI_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 1);
        params.put("prodId", 1010);
        params.put("prodName", "测试产品名称");
        params.put("serialNum", "1111111120");
        Result<String> result = thirdPayService.reqPay(ordCode, coreEpId, payType, params);
        rsp.setContentType("text/html; charset=UTF-8");
        logger.info(result.get());
        rsp.getWriter().print(result.get());
        rsp.getWriter().flush();
    }

    /* 测试方法，不要调用 */
    @RequestMapping(value = "/reqRefundTest")
    public void reqRefundTest(HttpServletResponse rsp) throws Exception {
        System.out.println("---------------------------->   /reqPay");
        long ordCode = 1111111120;
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.ALI_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 1);
        params.put("refundFee", 1);
        params.put("outTransId", "2016111021001004380274119776");
        params.put("serialNum", "1111111120");
        Result<String> result = thirdPayService.reqRefund(ordCode, coreEpId, payType, params);
        rsp.setContentType("text/html; charset=UTF-8");
        logger.info(result.get());
        rsp.getWriter().print(result.get());
        rsp.getWriter().flush();
    }

    /* 测试方法，不要调用 */
    @RequestMapping(value = "/wapPayTest")
    public void wapPayTest(HttpServletResponse rsp) throws Exception {
        System.out.println("---------------------------->   /wapPay");
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.ALI_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 1);
        params.put("prodId", 1010);
        params.put("prodName", "测试产品名称");
        params.put("serialNum", RandomStringUtils.random(10));
        Result<String> result = thirdPayService.wapPay(RandomUtils.nextLong(), coreEpId, payType, params);
        rsp.setContentType("text/html; charset=UTF-8");
        logger.info(result.get());
        rsp.getWriter().print(result.get());
        rsp.getWriter().flush();
    }
}
