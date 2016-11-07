package com.all580.base.controller.payment;

import com.all580.payment.api.conf.PaymentConstant;
import com.all580.payment.api.service.ThirdPayService;
import com.framework.common.BaseController;
import com.framework.common.Result;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 微信回调网关
 *
 * @author panyi on 2016/10/20.
 * @since V0.0.1
 */
@Controller
@RequestMapping("no_auth_api/callback/wx")
public class WxPayController extends BaseController {
    @Autowired
    private ThirdPayService thirdPayService;

    @RequestMapping(value = "/payment", method = {RequestMethod.POST})
    public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("微信支付回调->开始");

        InputStream inputStream = request.getInputStream();

        // 获取应答内容
        String resContent = new String(InputStreamTOByte(inputStream), "UTF-8");
        logger.info("微信回调内容：" + resContent);
        //解析xml,得到map
        Map m = XMLUtil.doXMLParse(resContent);
        if (m != null) {
            String ordCode = String.valueOf(m.get("out_trade_no"));
            String transactionId = String.valueOf(m.get("transaction_id"));

            Result<Map<String, String>> result = thirdPayService.payCallback(ordCode, transactionId, m, PaymentConstant
                    .PaymentType.WX_PAY);
            String rspXml = responseWx(result.get());
            logger.debug("微信回调 -> 返回：" + rspXml);
            response.getWriter().write(rspXml);
        } else {

        }
        logger.info("微信支付回调->结束");
    }

    private String responseWx(Map<String, String> rsp) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        String enc = "UTF-8";
        for (String key : rsp.keySet()) {
            sb.append("<" + key + ">" + "<![CDATA[" + rsp.get(key) + "]]></" + key + ">");
        }
        sb.append("</xml>");
        return new String(sb.toString().getBytes(), "ISO8859-1");
    }

    /**
     * InputStream转换成Byte
     * 注意:流关闭需要自行处理
     *
     * @param in
     * @return byte
     * @throws Exception
     */
    private static byte[] InputStreamTOByte(InputStream in) throws IOException {

        int BUFFER_SIZE = 4096;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;

        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        byte[] outByte = outStream.toByteArray();
        outStream.close();

        return outByte;
    }

    /* 测试方法，不要调用 */
    @RequestMapping(value = "/reqPayTest")
    public void reqPayTest(HttpServletResponse rsp) throws Exception {
        System.out.println("---------------------------->   /reqPayTest");
        long ordCode = 1111111113;
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.WX_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 1); // 单位分
        params.put("prodId", 1010);
        params.put("prodName", "测试产品名称");
        params.put("serialNum", "1111111113");
        Result<String> result = thirdPayService.reqPay(ordCode, coreEpId, payType, params);
        rsp.setContentType("text/html; charset=UTF-8");
        logger.info(result.get());
        rsp.getWriter().print(result.get());
        rsp.getWriter().flush();
    }

    /* 测试方法，不要调用 */
    @RequestMapping(value = "/reqRefundTest")
    public void reqRefundTest(HttpServletResponse rsp) throws Exception {
        System.out.println("---------------------------->   /reqRefundTest");
        long ordCode = 1111111113;
        int coreEpId = 1;
        int payType = PaymentConstant.PaymentType.WX_PAY;
        Map<String, Object> params = new HashMap<>();
        params.put("totalFee", 1);
        params.put("refundFee", 1);
        params.put("refundId", "1111111113");
        Result<String> result = thirdPayService.reqRefund(ordCode, coreEpId, payType, params);
        rsp.setContentType("text/html; charset=UTF-8");
        logger.info(result.get());
        rsp.getWriter().print(result.get());
        rsp.getWriter().flush();
    }

    /**
     * xml工具类
     *
     * @author miklchen
     */
    static class XMLUtil {

        /**
         * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
         *
         * @param strxml
         * @return
         * @throws JDOMException
         * @throws IOException
         */
        public static Map doXMLParse(String strxml) throws JDOMException, IOException {
            strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

            if (null == strxml || "".equals(strxml)) {
                return null;
            }

            Map m = new HashMap();

            InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v = "";
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = XMLUtil.getChildrenText(children);
                }

                m.put(k, v);
            }

            //关闭流
            in.close();

            return m;
        }

        /**
         * 获取子结点的xml
         *
         * @param children
         * @return String
         */
        public static String getChildrenText(List children) {
            StringBuffer sb = new StringBuffer();
            if (!children.isEmpty()) {
                Iterator it = children.iterator();
                while (it.hasNext()) {
                    Element e = (Element) it.next();
                    String name = e.getName();
                    String value = e.getTextNormalize();
                    List list = e.getChildren();
                    sb.append("<" + name + ">");
                    if (!list.isEmpty()) {
                        sb.append(XMLUtil.getChildrenText(list));
                    }
                    sb.append(value);
                    sb.append("</" + name + ">");
                }
            }

            return sb.toString();
        }

    }
}
