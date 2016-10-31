package com.all580.base.sign;

/**
 * Created by wxming on 2016/10/28 0028.
 */

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * getInputStream()和getReader() 都只能读取一次，由于RequestBody是流的形式读取，那么流读了一次就没有了，所以只能被调用一次。
 * 先将RequestBody保存，然后通过Servlet自带的HttpServletRequestWrapper类覆盖getReader()和getInputStream()方法，
 * 使流从保存的body读取。然后再Filter中将ServletRequest替换为ServletRequestWrapper
 * @description
 * @author yuhy
 * @date 2015年12月9日
 */
public class MyHttpServletRequest extends HttpServletRequestWrapper {


    public String get_body() {
        return _body;
    }

    private String _body;



    private HttpServletRequest _request;

    public MyHttpServletRequest(HttpServletRequest request) throws IOException
    {
        super(request);
        _request = request;

        StringBuffer jsonStr = new StringBuffer();
        try (BufferedReader bufferedReader = request.getReader())
        {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                jsonStr.append(line);
        }
        //获取到提交测json，将密码解密后重新复制给requestBody
        try{
            JSONObject json = JSONObject.parseObject(jsonStr.toString());
            _body = json.toJSONString();
            request.setAttribute("sign", json.get("sign"));
            request.setAttribute("_body",jsonStr.toString());
        }catch (Exception e){
            _body="";
        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes("UTF-8"));
        return new ServletInputStream()
        {
            public int read() throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.getInputStream(),"UTF-8"));
    }

}
