package com.all580.voucherplatform.filter;

import com.alibaba.fastjson.JSON;
import com.all580.voucherplatform.api.VoucherConstant;
import com.all580.voucherplatform.manager.BeanUtil;
import com.framework.common.Result;
import com.framework.common.io.cache.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Linv2 on 2017-07-11.
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private RedisUtils redisUtils;
    public static final int LOGINTIMEOUT = 1000 * 30;
    public static final String LOGINURL = "/voucher/api/user/login";
    public static final String POSURL = "/voucher/api/pos";
    public static final String MNSURL = "/voucher/api/mns/ticket";

    public AuthenticationFilter() {
        this.redisUtils = BeanUtil.getApplicationContext().getBean(RedisUtils.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        //  mns url  地址修改  通过后缀来过滤
        if (url.startsWith(LOGINURL) || url.startsWith(MNSURL) || url.startsWith(POSURL)) {
            filterChain.doFilter(request, response);
        } else {
            String sessionId = request.getSession().getId();
            if (StringUtils.isEmpty(sessionId)) {
                renderingByJsonPData(response,
                        JSON.toJSONString(getOutPutMap(false, "对不起，你还没有登录", Result.NO_PERMISSION, null)));
                return;
            }
//            Map mapUser = redisUtils.get(VoucherConstant.REDISVOUCHERLOGINKEY + ":" + sessionId, Map.class);
            Map mapUser = (Map) request.getSession().getAttribute("user");
            if (mapUser != null) {
//                redisUtils.expire(VoucherConstant.REDISVOUCHERLOGINKEY + ":" + sessionId, LOGINTIMEOUT);
                request.setAttribute("user", mapUser);
                filterChain.doFilter(request, response);
            } else {
                renderingByJsonPData(response,
                        JSON.toJSONString(getOutPutMap(false, "对不起，登录超时", Result.NO_PERMISSION, null)));
                return;
            }
        }
    }

    /**
     * 输出result中的键值只response中，值需为jsonp格式
     *
     * @param response
     * @param str
     */
    private void renderingByJsonPData(HttpServletResponse response,
                                      String str) {
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

    public Map getOutPutMap(boolean success,
                            String message,
                            Integer code,
                            String result) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        map.put("code", code);
        map.put("result", result);
        return map;
    }
}
