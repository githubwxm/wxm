package com.all580.payment.thirdpay.wx.service;

import com.all580.payment.thirdpay.wx.client.TenpayHttpClient;
import com.all580.payment.thirdpay.wx.model.AccessTokenBean;
import com.all580.payment.thirdpay.wx.model.WxProperties;
import com.all580.payment.thirdpay.wx.util.ConstantUtil;
import com.framework.common.distributed.lock.DistributedLockTemplate;
import com.framework.common.distributed.lock.DistributedReentrantLock;
import com.framework.common.io.cache.redis.RedisUtils;
import com.framework.common.lang.JsonUtils;
import com.framework.common.net.HttpUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.lang.exception.ApiException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author zhouxianjun(Alone)
 * @ClassName:
 * @Description:
 * @date 2017/3/23 14:31
 */
@Component
@Slf4j
public class AccessToken {
    @Autowired
    private DistributedLockTemplate distributedLockTemplate;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${web.wx.url}")
    private String webWxUrl;
    @Value("${web.wx.host}")
    private String webWxHost;

    public static final String TOKEN = "token";

    public AccessTokenBean get(WxProperties wxProperties) {
        DistributedReentrantLock lock = distributedLockTemplate.execute(wxProperties.getApp_id(), 60);
        try {
            AccessTokenBean bean = redisUtils.get(TOKEN + ":" + wxProperties.getApp_id(), AccessTokenBean.class);
            if (bean == null || bean.getExpires().before(new Date())) {
                bean = refresh(wxProperties);
            }
            return bean;
        } finally {
            lock.unlock();
        }
    }

    public AccessTokenBean getForWeb(WxProperties wxProperties) {
        Map map = getAccToken(wxProperties);
        AccessTokenBean bean = new AccessTokenBean();
        bean.setAppId(wxProperties.getApp_id());
        bean.setToken(map.get("access_token").toString());
        bean.setExpiresSecond(CommonUtil.objectParseInteger(map.get("expires_in")));
        Date last = new Date();
        bean.setExpires(DateUtils.addSeconds(last, (int) (bean.getExpiresSecond() * 0.9))); // 90%的时候刷新
        bean.setLast(last);
        bean.setTicket(getTicket(wxProperties));
        return bean;
    }

    public AccessTokenBean refresh(WxProperties wxProperties) {
        DistributedReentrantLock lock = distributedLockTemplate.execute(wxProperties.getApp_id() + "$refresh", 60);
        try {
            TenpayHttpClient client = new TenpayHttpClient();
            client.setIsHttps(true);
            client.setMethod("get");
            client.setReqContent(String.format("%s?grant_type=client_credential&appid=%s&secret=%s", ConstantUtil.TOKENURL, wxProperties.getApp_id(), wxProperties.getApp_secret()));
            client.setWxProperties(wxProperties);
            if (!client.call()){
                log.warn("微信获取Token通信异常: {}-{}", client.getResponseCode(), client.getErrInfo());
                throw new RuntimeException("微信获取Token通信失败");
            }
            String response = client.getResContent();
            log.info("微信获取Token返回: {}", response);
            Map map = JsonUtils.json2Map(response);
            if (!map.containsKey("access_token")) {
                throw new RuntimeException("微信获取Token失败");
            }
            AccessTokenBean bean = new AccessTokenBean();
            bean.setAppId(wxProperties.getApp_id());
            bean.setToken(map.get("access_token").toString());
            bean.setExpiresSecond(CommonUtil.objectParseInteger(map.get("expires_in")));
            Date last = new Date();
            bean.setExpires(DateUtils.addSeconds(last, (int) (bean.getExpiresSecond() * 0.9))); // 90%的时候刷新
            bean.setLast(last);
            getTicket(bean, wxProperties);
            redisUtils.setex(TOKEN + ":" + wxProperties.getApp_id(), bean.getExpiresSecond(), bean);
            return bean;
        } finally {
            lock.unlock();
        }
    }

    private void getTicket(AccessTokenBean bean, WxProperties wxProperties) {
        TenpayHttpClient client = new TenpayHttpClient();
        client.setIsHttps(true);
        client.setMethod("get");
        client.setReqContent(String.format("%s?access_token=%s&type=jsapi", ConstantUtil.TICKETURL, bean.getToken()));
        client.setWxProperties(wxProperties);
        if (!client.call()){
            log.warn("微信获取Ticket通信异常: {}-{}", client.getResponseCode(), client.getErrInfo());
            throw new RuntimeException("微信获取Ticket通信失败");
        }
        String response = client.getResContent();
        log.info("微信获取Ticket返回: {}", response);
        Map map = JsonUtils.json2Map(response);
        Object ticket = map.get("ticket");
        if (ticket == null || StringUtils.isEmpty(ticket.toString())) {
            throw new RuntimeException("微信获取Ticket失败");
        }
        bean.setTicket(ticket.toString());
    }

    private String postAll580Wx(String method, Map params, String accessKey) {
        String json = JsonUtils.toJson(params);
        String sign = DigestUtils.md5Hex(json + accessKey);
        return HttpUtils.postJson(String.format("%s/%s?wxhost=%s&sign=%s", webWxUrl, method, webWxHost, sign), json, "UTF-8", true);
    }

    public Map getAccToken(WxProperties wxProperties) {
        try {
            Map<String, String> params = Collections.singletonMap("access_id", wxProperties.getWeb_access_id());
            String response = postAll580Wx("getAccToken", params, wxProperties.getWeb_access_key());
            log.info("微信获取Token返回: {}", response);
            Map map = JsonUtils.json2Map(response);
            if (!map.containsKey("access_token")) {
                throw new ApiException("access_token 为空:" + response);
            }
            return map;
        } catch (Exception e) {
            throw new ApiException("获取微信token失败", e.getMessage());
        }
    }
    public String getTicket(WxProperties wxProperties) {
        try {
            Map<String, String> params = Collections.singletonMap("access_id", wxProperties.getWeb_access_id());
            String response = postAll580Wx("getJsTicket", params, wxProperties.getWeb_access_key());
            log.info("微信获取ticket返回: {}", response);
            Map map = JsonUtils.json2Map(response);
            Object ticket = map.get("ticket");
            if (ticket == null || StringUtils.isEmpty(ticket.toString())) {
                throw new ApiException("ticket 为空:" + response);
            }
            return ticket.toString();
        } catch (Exception e) {
            throw new ApiException("获取微信ticket失败", e.getMessage());
        }
    }
}
