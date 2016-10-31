package com.all580.base.sign;

import com.alibaba.fastjson.JSON;

import com.all580.ep.api.service.CoreEpAccessService;
import com.framework.common.Result;
import com.framework.common.lang.JsonUtils;
import com.framework.common.util.CommonUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.lang.exception.ApiException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wxming on 2016/10/24 0024.
 */
public class VerifyDataInterceptor extends HandlerInterceptorAdapter {

//    @Autowired
//    private CoreEpAccessService coreEpAccessService;
//
//    private void test(HttpServletRequest request, HttpServletResponse response, Object handler){
//        Map map = new HashMap();
//        map.put("access_id","1476277249859N2T3JBGA");
//        Map access = coreEpAccessService.selectAccess(map).get();
//        if (access.isEmpty()) {
//            throw new ApiException("数据校验失败");
//            //return false;
//        } else {
//            request.setAttribute("core_ep_id", access.get("id"));
//            String key = access.get("access_key").toString();
//            request.setAttribute("access_key",key);
//        }
//    }
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//        if(1==2){
//            test(request,response,handler);
//            return true;
//        }
////        ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
////        String body = HttpHelper.getBodyString(requestWrapper);
//
//          String sign= CommonUtil.objectParseString(request.getParameter("sign"));
//        String method = request.getMethod();
//        String postParams = "";
//        String url = request.getRequestURI();
//        String currenttSing = "";//
//        Map<String, Object> map = new HashedMap();
//        if ("POST".equals(method)) {
//
//            ServletRequest requestWrapper = null;
//            if(request instanceof HttpServletRequest) {
//                requestWrapper = new MyHttpServletRequest((HttpServletRequest) request);
//            }
//            // postParams = SignVerify.getRequestBody(request.getInputStream());//Post
//            try {
//                byte[] bytes=StreamUtils.copyToByteArray(requestWrapper.getInputStream());
//                postParams = new String(bytes, "UTF-8");
//                service(requestWrapper,response);
//                request =(HttpServletRequest) requestWrapper;
//            }catch(IOException e){
//
//            }
//
//            map = JsonUtils.json2Map(postParams);
//        } else if ("GET".equals(method)) {
//            map = SignVerify.getParameterMap(request);//get 获取
//        }
//        if (map.size() == 0) {
//            return false;
//        }//            map=JsonUtils.obj2map(map);
//        currenttSing = map.remove("sign").toString();//获取当前传来的加密// 去掉sing
//        TreeMap tree=new TreeMap(map);
//        postParams = JsonUtils.toJson(tree);//传来的参数 去掉sing
//        Map access = coreEpAccessService.selectAccess(map).get();
//        if (access.isEmpty()) {
//            throw new ApiException("数据校验失败");
//            //return false;
//        } else {
//            request.setAttribute("core_ep_id", access.get("id"));
//            String key = access.get("access_key").toString();
//
//            boolean ref = SignVerify.verifyPost(postParams, currenttSing, key);
//            if (!ref) {
//                renderingByJsonPData(response,JSON.toJSONString(getOutPutMap(false,"签名校验失败",Result.SIGN_FAIL,null)));
//            }
//            request.setAttribute("access_key",key);
//
//            //postParams = SignVerify.getRequestBody(request.getInputStream());//Post
//            return true;//return ref;//校验数据
//        }
//    }
//    public Map getOutPutMap(boolean success,String message,Integer code,String result) {
//        Map map = new HashMap();
//        map.put("success", success);
//        map.put("message", message);
//        map.put("code", code);
//        map.put("result", result);
//        return map;
//    }
//
//    /**
//     * 输出result中的键值只response中，值需为jsonp格式
//     * @param response
//     * @param
//     */
////    public void renderingByJsonPData(HttpServletResponse response, String str) {
////        if (response.getContentType() == null)
////            response.setContentType("text/html; charset=UTF-8");
////        PrintWriter writer = null;
////        try {
////         //   String str = JsonUtils.toJsonP(functionName, toJsonString());
////            writer = response.getWriter();
////            writer.print(str);
////            writer.flush();
////        } catch (Exception e) {
////        } finally {
////            if (writer != null) {
////                writer.close();
////            }
////        }
////    }
//
//    protected void service(ServletRequest request, HttpServletResponse response) throws ServletException, IOException
//    {
//
//        DataInputStream in = new DataInputStream(request.getInputStream());
//        request.getInputStream().reset();
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json;charset=UTF-8");
//        StringBuffer jb = new StringBuffer();
//        String line = null;
//        try
//        {
//            BufferedReader reader = request.getReader();
//            while ((line = reader.readLine()) != null)
//                jb.append(line);
//            reader.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        System.out.println(jb);
//
//
//        PrintWriter out = response.getWriter();
//        out.append(jb);
//        out.close();
//    }
}
