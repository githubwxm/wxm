package com.all580.base.sign;

import com.alibaba.fastjson.JSON;
import com.all580.base.manager.BeanUtil;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.CoreEpAccessService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;

import javax.lang.exception.ApiException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wxming on 2016/10/13 0013.
 */
@Slf4j
public class VerifyFilter implements  Filter{
//    @Autowired
//    private CoreEpAccessService coreEpAccessService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse =(HttpServletResponse)response;
        String method= httpRequest.getMethod();
        Map<String, Object> map = new HashedMap();
        ServletRequest requestWrapper = null;
        String postParams="";
        String currenttSing="";
       // String url =httpRequest.getRequestURI(); // 访问url
        CoreEpAccessService coreEpAccessService= BeanUtil.getBean("coreEpAccessService", CoreEpAccessService.class);

        if ("POST".equals(method)) {
            if(request instanceof HttpServletRequest) {
                requestWrapper = new MyHttpServletRequest((HttpServletRequest) request);
                postParams= CommonUtil.objectParseString(requestWrapper.getAttribute("_body"));
                postParams=postParams==null?"":postParams;
                map = JsonUtils.json2Map(postParams);

            }
        }else if ("GET".equals(method)) {
            map = SignVerify.getParameterMap(httpRequest);//get 获取

        }
        currenttSing =CommonUtil.objectParseString( map.remove("sign"));//获取当前传来的加密// 去掉sing
        if(null==map.get("access_id")){
            log.error("access_id不能为空");
            renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"access_id不能为空", Result.SIGN_FAIL,null)));
            return;
        }
        Map access=null;
        try {
             access = coreEpAccessService.selectAccess(map).get();
        }catch (Exception e){
            log.error(e.getMessage());
            renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"获取access_key错误", Result.SIGN_FAIL,null)));
            return;
        }
        if (access.isEmpty()) {
            log.error("获取access_key失败");
            renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"获取access_key失败", Result.SIGN_FAIL,null)));
            return;
        } else {
            try{
                request.setAttribute(EpConstant.EpKey.CORE_EP_ID, access.get("id"));
                String key = access.get("access_key").toString();
                request.setAttribute(EpConstant.EpKey.ACCESS_KEY,key);
                TreeMap tree=new TreeMap(map);
                postParams = JsonUtils.toJson(tree);
                boolean ref = SignVerify.verifyPost(postParams, currenttSing, key);
                if (!ref) {
                    log.error("签名校验失败");
                    renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"签名校验失败", Result.SIGN_FAIL,null)));
                    return;
                }else{
                    if(null == requestWrapper) {
                        chain.doFilter(request, response);
                    } else {
                        chain.doFilter(requestWrapper, response);
                    }
                }
            }catch(Exception e){
                renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"过滤异常未被捕获", Result.SIGN_FAIL,null)));
                log.error(e.getMessage());
                return;
            }
        }

    }

    public Map getOutPutMap(boolean success,String message,Integer code,String result) {
        Map<String,Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        map.put("code", code);
        map.put("result", result);
        return map;
    }

    /**
     * 输出result中的键值只response中，值需为jsonp格式
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


    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }


}
