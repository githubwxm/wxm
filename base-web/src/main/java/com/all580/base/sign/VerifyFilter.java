package com.all580.base.sign;
import javax.servlet.*;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by wxming on 2016/10/13 0013.
 */
public class VerifyFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
              request.getParameter("");
    }

    @Override
    public void destroy() {

    }
}
