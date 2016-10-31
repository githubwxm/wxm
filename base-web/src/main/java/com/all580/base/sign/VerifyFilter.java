package com.all580.base.sign;

import com.alibaba.fastjson.JSON;
import com.all580.base.manager.BeanUtil;
import com.all580.ep.api.conf.EpConstant;
import com.all580.ep.api.service.CoreEpAccessService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
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
        String url =httpRequest.getRequestURI();
        CoreEpAccessService coreEpAccessService= BeanUtil.getBean("coreEpAccessService", CoreEpAccessService.class);

//        String a = httpRequest.getParameter("access_id");
//        String url =httpRequest.getRequestURI();
//        Enumeration<?> names = request.getParameterNames();
//        while(names.hasMoreElements()){
//            System.out.println(names.nextElement());
//        }
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
        Map access = coreEpAccessService.selectAccess(map).get();
        if (access.isEmpty()) {
            throw new ApiException("数据校验失败");
            //return false;
        } else {
            request.setAttribute(EpConstant.EpKey.CORE_EP_ID, access.get("id"));
            String key = access.get("access_key").toString();
            request.setAttribute(EpConstant.EpKey.ACCESS_KEY,key);
            TreeMap tree=new TreeMap(map);
        postParams = JsonUtils.toJson(tree);
            boolean ref = SignVerify.verifyPost(postParams, currenttSing, key);
            if (!ref) {
                renderingByJsonPData(httpResponse, JSON.toJSONString(getOutPutMap(false,"签名校验失败", Result.SIGN_FAIL,null)));
            }else{
                if(null == requestWrapper) {
                    chain.doFilter(request, response);
                } else {
                    chain.doFilter(requestWrapper, response);
                }
            }
        }

    }

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
