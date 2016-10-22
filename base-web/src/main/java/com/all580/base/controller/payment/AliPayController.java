package com.all580.base.controller.payment;

import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import org.apache.commons.httpclient.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author panyi on 2016/10/20.
 * @since V0.0.1
 */
@Controller
@RequestMapping("callback/ali")
public class AliPayController extends BaseController {
    @Resource
    private ThirdPayService thirdPayService;

    /**
     * 退款回调
     *
     * @throws IOException
     */
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public void refund(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
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

        String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");

        //批量退款数据中转账成功的笔数

        String success_num = new String(request.getParameter("success_num").getBytes("ISO-8859-1"), "UTF-8");

        //批量退款数据中的详细信息
        String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"), "UTF-8");
        String sign = new String(request.getParameter("sign").getBytes("ISO-8859-1"), "UTF-8");
//        String[] transStr = result_details.split("#");
//        List<Map<String, String>> resultMaps = new ArrayList<Map<String, String>>();
//        for (String trans : transStr) {
//            String[] details = trans.split("\\^");
//            Map<String, String> maps = new HashMap<String, String>();
//            maps.put("status", details[2]);
//            maps.put("money", details[1]);
//            maps.put("outTransId", details[0]);
//            resultMaps.add(maps);
//        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

        Result result = thirdPayService.refundCallback(params,PaymentConstant.PaymentType.ALI_PAY);
        if (result.isSuccess()) {//验证成功

            rsp.getWriter().println("success");    //请不要修改或删除

        } else {//验证失败
            rsp.getWriter().println("fail");
        }
    }


    /**
     * 支付回调
     *
     * @throws IOException
     */
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public void payment(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
        logger.info("支付宝支付回调开始");
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
        Result result = thirdPayService.payCallback(ordId, trade_no, params,PaymentConstant.PaymentType.ALI_PAY);
        if (result.isSuccess()) {//验证成功

            rsp.getWriter().println("success");    //请不要修改或删除

            //////////////////////////////////////////////////////////////////////////////////////////
        } else {//验证失败
            rsp.getWriter().println("fail");
        }
        logger.info("支付宝支付回调结束");
    }
}
