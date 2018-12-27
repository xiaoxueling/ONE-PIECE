package cn.core.seo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SEOInterceptor implements HandlerInterceptor {

	SEOHelper seoHelper;
	
	public SEOInterceptor(SEOHelper seoHelper) {
		this.seoHelper=seoHelper;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		try{
			if(modelAndView!=null){
				SEOContext context=seoHelper.getSEO();
				modelAndView.addObject("SEO", context);
			}
		}catch(Exception ex){
			
		}
	}
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
