package com.all580.base.sign;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by wangxm on 2016/10/13 0013.
 */
public class SignVerify {

    private static DecimalFormat df = null;

    public static String charset = "utf-8";


    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
    public static boolean verifyPost(String params, String sign, String key) {
        String resultSign= toMD5(params,key);
        return sign.equals(resultSign);
    }

//    public static void main(String agrs[]) {
//
//        String value="{access_id:1476425987296LFJFURKK,address:1212,area:120102,area_name:河东区,city:120100,city_name:市辖区,code:1212,ep_id:30,license:13212,link_phone:13417325939,linkman:1,logo_pic:star/upls/2016/10/25/580f24946fa4d.png,name:121212,operator_id:9,operator_name:ZOUJING,province:120000,province_name:天津市}";
//
//        String key="1476425996329CJCJ6VXG3YMCNB";
//        System.out.println(verifyPost(value,"",key));
//
//
////	       new TestMain().toMD5("123456789dfadefq123456789dfadefq123456789d","key");//加密LXD
////	       Object obj=null;
////	       String  s = (String) obj;
////	       System.out.println(s);
//        // JSONObject jsonObject = JSONObject.fromObject(productMap);
//
//    }




    public static String toMD5(String plainText,String key) {
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要。
            md.update(plainText.getBytes("UTF-8"));
            //通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest(key.getBytes());
            //生成具体的md5密码到buf数组
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();// 32位的加密
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * post 方法获取流中的数据
     * @param stream
     * @return
     */
    public static String getRequestBody(InputStream stream) {
        String line = "";
        StringBuilder body = new StringBuilder();
        int counter = 0;

        // 读取POST提交的数据内容
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream,charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            while ((line = reader.readLine()) != null) {
                if (counter > 0) {
                    body.append("rn");
                }
                body.append(line);
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    /**
     * post 方法获取流中的数据
     * @param stream
     * @return
     */
    public static String getResponseBody(OutputStream stream) {
        String line = "";
        StringBuilder body = new StringBuilder();
        int counter = 0;
        String a =stream.toString();
        // 读取POST提交的数据内容
        BufferedReader reader = null;
//        try {
//           reader = new BufferedReader(new OutputStream(stream,charset));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        try {
//            while ((line = reader.readLine()) != null) {
//                if (counter > 0) {
//                    body.append("rn");
//                }
//                body.append(line);
//                counter++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
   // }
        return body.toString();
    }

}
