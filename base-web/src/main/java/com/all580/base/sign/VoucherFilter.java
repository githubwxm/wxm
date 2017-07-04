package com.all580.base.sign;

import com.alibaba.fastjson.JSON;
import com.all580.base.manager.BeanUtil;
import com.all580.voucherplatform.api.VoucherConstant;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-06-29.
 */
public class VoucherFilter extends OncePerRequestFilter {
    private RedisUtils redisUtils;

    public VoucherFilter() {
        this.redisUtils = BeanUtil.getApplicationContext().getBean(RedisUtils.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (!url.equals("/voucherplatform/user/login")) {
            String token = getCookie(request, VoucherConstant.COOKIENAME);
            if (StringUtils.isEmpty(token)) {
                renderingByJsonPData(response, JSON.toJSONString(getOutPutMap(false, "对不起，你还没有登录", Result.NO_PERMISSION, null)));
                return;
            }
            Map mapUser = redisUtils.get(VoucherConstant.REDISVOUCHERLOGINKEY + ":" + token, Map.class);
            if (mapUser != null) {
                request.setAttribute("user", mapUser);
                filterChain.doFilter(request, response);
                return;
            }
            renderingByJsonPData(response, JSON.toJSONString(getOutPutMap(false, "对不起，登录超时", Result.NO_PERMISSION, null)));
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 输出result中的键值只response中，值需为jsonp格式
     *
     * @param response
     * @param str
     */
    private void renderingByJsonPData(HttpServletResponse response, String str) {
        if (response.getContentType() == null)
            response.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = null;
        try {
            //   String str = JsonUtils.toJsonP(functionName, toJsonString());
            writer = response.getWriter();
            writer.print(str);
            writer.flush();
        } catch (Exception e) {
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public Map getOutPutMap(boolean success, String message, Integer code, String result) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        map.put("code", code);
        map.put("result", result);
        return map;
    }

    private String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
