package com.all580.voucherplatform.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpRequestBase;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 美团BA认证
 * Created by alone on 17-4-7.
 */
@Slf4j
public class BasicAuthorizationUtils {

    private static final String ALGORITHM_HMAC_SHA1 = "HmacSHA1";
    public static final String HTTP_HEADER_PARTNERID = "PartnerId";
    public static final String HTTP_HEADER_DATE = "Date";
    public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    public static final String MEITUAN_AUTH_METHOD = "MWS";
    private static final String HTTP_HEADER_TIME_ZONE = "GMT";
    private static final String HTTP_HEADER_DATE_FORMAT = "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'z";

    /**
     * 生成请求头认证信息
     * @param request
     * @param clientId
     * @param clientSecret
     */
    public static void generateAuthAndDateHeader(HttpRequestBase request, String clientId,
                                                 String clientSecret) {
        Date sysdate = new Date();
        SimpleDateFormat df = new SimpleDateFormat(HTTP_HEADER_DATE_FORMAT, Locale.US);
        df.setTimeZone(TimeZone.getTimeZone(HTTP_HEADER_TIME_ZONE));
        String date = df.format(sysdate);
        String stringToSign = request.getMethod().toUpperCase() + " " +
                request.getURI().getPath() +
                "\n" + date;
        String encoding;
        try {
            encoding = getSignature(stringToSign.getBytes(), clientSecret.getBytes());
        } catch (Exception e1) {
            log.warn("Signature Exception occured:", e1);
            return;
        }
        String authorization = MEITUAN_AUTH_METHOD + " " + clientId + ":" + encoding;
        request.addHeader(HTTP_HEADER_AUTHORIZATION, authorization);
        request.addHeader(HTTP_HEADER_DATE, date);
        log.debug("美团BA认证:before:{},signature:{},headers:{}", new Object[]{stringToSign, encoding, Arrays.toString(request.getAllHeaders())});
    }

    /**
     * 生产签名
     * @param data
     * @param key
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static String getSignature(byte[] data, byte[] key)
            throws InvalidKeyException,NoSuchAlgorithmException {
        SecretKeySpec signingKey = new SecretKeySpec(key, ALGORITHM_HMAC_SHA1);
        Mac mac = Mac.getInstance(ALGORITHM_HMAC_SHA1);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data);
        return new String(Base64.encodeBase64(rawHmac));
    }
}