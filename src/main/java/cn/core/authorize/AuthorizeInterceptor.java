package cn.core.authorize;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Service
public class AuthorizeInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			HandlerMethod hm = (HandlerMethod) handler;
			Class<?> clazz = hm.getBeanType();
			Method m = hm.getMethod();
			if (clazz != null && m != null) {
				if (m.isAnnotationPresent(AllowAnonymous.class)) {
					return true;
				}
				boolean isClzAnnotation = clazz.isAnnotationPresent(Authorize.class);
				boolean isMethondAnnotation = m.isAnnotationPresent(Authorize.class);
				Authorize authorize = null;
				if (isMethondAnnotation) {
					authorize = m.getAnnotation(Authorize.class);
				} else if (isClzAnnotation) {
					authorize = clazz.getAnnotation(Authorize.class);
				}
				if (authorize == null) {
					return true;
				}
				// TODO:权限逻辑验证
				if (AuthorizeVerifier.has(request.getSession(), authorize)) {
					return true;
				}
				request.getRequestDispatcher("/error/notallow").forward(request, response);
				return false;
			}

		}
		return true;
	}
}
