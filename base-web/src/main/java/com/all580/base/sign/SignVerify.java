package com.all580.base.sign;

import com.framework.common.util.CommonUtil;
import org.apache.commons.io.IOUtils;

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

    public final static String charset = "utf-8";


    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
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

    /**
     * 确认签名是否一致
     * @param params
     * @param sign
     * @param key
     * @return
     */
    public static boolean verifyPost(String params, String sign, String key) {
        String resultSign= CommonUtil.signForData(key,params);
        return sign.equals(resultSign);
    }

//    /**
//     * 加密
//     * @return
//     */
//    public static Result  verify(){
//
//    }

    public static String toMD5(String plainText,String key) {
        try {
            //生成实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            //使用指定的字节数组更新摘要。
            md.update(plainText.getBytes("UTF-8"));
            //通过执行诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest(key.getBytes(charset));
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
//        finally{
//            IOUtils.closeQuietly(reader);
//        }

        return body.toString();
    }


}
