package cn.core.interceptor;

import java.lang.reflect.Method;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.core.interceptor.UserValidate.PlatForm;
import cn.util.DataConvert;
import cn.util.StringHelper;

/**
 * 前台登录拦截器
 * @author xiaoxueling
 *
 */
public class UserValidateInterceptor extends HandlerInterceptorAdapter{
	
	//登录平台
	private PlatForm platForm;
	
	public UserValidateInterceptor() {
		this(PlatForm.PC);
	}
	
	public UserValidateInterceptor(PlatForm platForm) {
		this.platForm=platForm;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		
		request.setCharacterEncoding("UTF-8"); 
		HttpSession session=request.getSession();
		
		if(handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			
			HandlerMethod hm = (HandlerMethod) handler;
			Class<?> clazz = hm.getBeanType();
			Method m = hm.getMethod();
			if (clazz != null && m != null) {
				if (m.isAnnotationPresent(UserValidate.class) || clazz.isAnnotationPresent(UserValidate.class)) {
					
					Integer userId=DataConvert.ToInteger(session.getAttribute("userId"));
					if(userId!=null&&userId>0){
						return true;
					}
					String loginUrl="";
					switch (platForm) {
						case PC:
							
							loginUrl="/web/login/index";
							
							break;
						case Mobile:
							
							//是否是微信登录
							boolean isWechat=!StringHelper.IsNullOrEmpty(DataConvert.ToString(session.getAttribute("openId")));
							loginUrl=isWechat?"/phone/allow/login":"/phone/allow/login";
							
							break;
					}
					if(!StringHelper.IsNullOrEmpty(loginUrl)) {
						
						String uri = request.getRequestURI();
						String query = request.getQueryString();
						if (!StringUtils.isEmpty(query)) {
							uri += "?" + query;
						}
						
						request.getRequestDispatcher(loginUrl+"?redirectUrl=" + URLEncoder.encode(uri, "UTF-8")).forward(request, response);
						return false;
					}
				}
			}
		}
		return true;
	}
}
