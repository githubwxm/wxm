package com.all580.base.sign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.all580.ep.api.service.CoreEpAccessService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.lang.exception.ApiException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wxming on 2016/10/24 0024.
 */
public class VerifyDataInterceptor implements HandlerInterceptor {

    @Autowired
    private CoreEpAccessService coreEpAccessService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String method = request.getMethod();
        String postParams = "";
        String url = request.getRequestURI();
        String currenttSing = "";//
        Map<String, Object> map = new HashedMap();
        if ("POST".equals(method)) {
            postParams = SignVerify.getRequestBody(request.getInputStream());//Post
            map = JsonUtils.json2Map(postParams);
        } else if ("GET".equals(method)) {
            map = SignVerify.getParameterMap(request);//get 获取
        }
        if (map.size() == 0) {
            return false;
        }
        currenttSing = map.remove("sign").toString();//获取当前传来的加密
        // 去掉sing
        postParams = JSON.toJSONString(map);//传来的参数 去掉sing
        Map access = coreEpAccessService.selectAccess(map).get();
        if (access.isEmpty()) {
            throw new ApiException("数据校验失败");
            //return false;
        } else {
            request.setAttribute("core_ep_id", access.get("id"));
            String key = access.get("access_key").toString();
            postParams=postParams.replace("\"","");
            postParams=postParams.replace("\\","");
            boolean ref = SignVerify.verifyPost(postParams, currenttSing, key);
            if (!ref) {
                renderingByJsonPData(response,JSON.toJSONString(getOutPutMap(false,"签名校验失败",Result.SIGN_FAIL,null)));
            }
            return ref;//校验数据
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        //添加校验
        response.setHeader("content-type", "text/html;charset=UTF-8");
        ServletOutputStream out=response.getOutputStream();

        SignVerify.getResponseBody(out);
        System.out.println("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    @JsonIgnore
    public Map getOutPutMap(boolean success,String message,Integer code,String result) {
        Map map = new HashMap();
        map.put("success", success);
        map.put("message", message);
        map.put("code", code);
        map.put("result", result);
        return map;
    }

    /**
     * 输出result中的键值只response中，值需为jsonp格式
     *
     * @param response
     * @param str
     */
    public void renderingByJsonPData(HttpServletResponse response, String str) {
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
}
