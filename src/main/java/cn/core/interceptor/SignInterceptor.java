package cn.core.interceptor;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.util.Tools;

public class SignInterceptor extends HandlerInterceptorAdapter {
	
	private String securityKey;
	
	public SignInterceptor(){
		this("qfsdfsdfasd");
	}
	
	public SignInterceptor(String securityKey){
		this.securityKey=securityKey;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		request.setCharacterEncoding("UTF-8");  
		
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			HandlerMethod hm = (HandlerMethod) handler;
			Class<?> clazz = hm.getBeanType();
			Method m = hm.getMethod();
			if (clazz != null && m != null) {
				if (m.isAnnotationPresent(IgnoreSign.class)) {
					return true;
				}
				if (m.isAnnotationPresent(Sign.class) || clazz.isAnnotationPresent(Sign.class)) {
					if (request.getParameter("sign") == null) {
						return false;
					}
					//TreeMap<String, String> map=new TreeMap<String, String>();
	        		//改成无序的
					Map<String, String> map=new LinkedHashMap<String,String>();
					Enumeration<String> names = request.getParameterNames();
					String sign = "";
	        		while (names.hasMoreElements()) {
	        			String name= names.nextElement()+"";
	        			if (name.compareTo("sign")==0) {
	        				sign=request.getParameter("sign");
	        				continue;
	        			}
	        			map.put(name, request.getParameter(name));
	        		}
	        		
	        		String signData="";
	        	
	        		for (Map.Entry<String, String> item:map.entrySet()) {
	        			signData+=item.getKey()+item.getValue();
					}

					signData+=securityKey;
	        		String md5Str=Tools.Md5(signData);
					
	        		if (!md5Str.equalsIgnoreCase(sign)) {
	        			response.setContentType("application/json;charset=utf-8"); 
	        			PrintWriter out = response.getWriter();
		       	        out.print("{\"result\":0,\"msg\":\"请求被禁止！\"}");
		       	        out.close();
	        			
	        			return false;
	        		}
				}
			}
		}

		return true;
	}
}
