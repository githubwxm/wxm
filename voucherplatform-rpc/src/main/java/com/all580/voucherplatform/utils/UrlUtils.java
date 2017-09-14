package com.all580.voucherplatform.utils;

import com.all580.voucherplatform.entity.ShortDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Linv2 on 2016-11-10 0010.
 */
public class UrlUtils {

    static String SHORTURL_SERVER = "http://m8e.cn/UriService/Create";

    public static List<ShortDTO> ShortUriAPI(String... urls) {
        List<ShortDTO> map = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            byte[] reqBuffer = objectMapper.writeValueAsBytes(urls);
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(SHORTURL_SERVER);

            EntityBuilder entityBuilder = EntityBuilder.create();
            entityBuilder.setBinary(reqBuffer);
            entityBuilder.setContentType(ContentType.APPLICATION_JSON);
            httpPost.setEntity(entityBuilder.build());
            HttpResponse httpResponse = httpclient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                InputStream inputStream = httpResponse.getEntity().getContent();
                int length = (int) httpResponse.getEntity().getContentLength();
                byte[] rspBuffer = new byte[length];
                inputStream.read(rspBuffer, 0, length);
                String json = new String(rspBuffer, "utf-8");
                map = objectMapper.readValue(json, new TypeReference<List<ShortDTO>>() {
                });
            }

        } catch (Exception ex) {
            return map;
        }
        return map;
    }

    public static byte[] RequestUrl(String url) throws Exception {
        byte[] rspBuffer = null;
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpclient.execute(httpGet);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            InputStream inputStream = httpResponse.getEntity().getContent();
            int length = (int) httpResponse.getEntity().getContentLength();
            rspBuffer = new byte[length];
            inputStream.read(rspBuffer, 0, length);
            return rspBuffer;
        }
        return rspBuffer;

    }

    public static String shortUri(String url) {
        List<ShortDTO> map = ShortUriAPI(url);
        if (map == null || map.size() == 0) {
            return url;
        }
        String shortKey = map.get(0).Value;
        try {
            URIBuilder uriBuilder = new URIBuilder(SHORTURL_SERVER);
            uriBuilder.setPath("/" + shortKey);
            return uriBuilder.toString();
        } catch (Exception ex) {
            return url;
        }
    }


}

