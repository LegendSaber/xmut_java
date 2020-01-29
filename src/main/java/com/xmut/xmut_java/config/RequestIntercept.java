package com.xmut.xmut_java.config;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import com.xmut.xmut_java.common.Result;

public class RequestIntercept implements HandlerInterceptor{
	@SuppressWarnings({ "unused", "finally" })
	private boolean setResponse(HttpServletResponse response, Result result) throws IOException {
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/html; charset=utf-8");
	    OutputStream output = null;
	    try {
	        output = response.getOutputStream();
	        output.write(JacksonUtil.getObjectMapper().writeValueAsString(result).getBytes("UTF-8"));
	    } catch (IOException e) {
	        throw new RuntimeException(e.getMessage(), e);
	    } finally {
	        if (output != null){
	            output.close();
	        }
	        return false;
	    }
	}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	 //请求路径
        @SuppressWarnings("unused")
		String requestURI = request.getRequestURI();
        
    	return true;
    }
}
