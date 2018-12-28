package cn.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.core.UploadServlet;
import cn.core.authorize.AuthorizeInterceptor;
import cn.core.interceptor.SignInterceptor;
import cn.core.interceptor.StaticHtmlInterceptor;
import cn.core.interceptor.UserValidate.PlatForm;
import cn.core.seo.SEOHelper;
import cn.core.seo.SEOInterceptor;
import cn.core.interceptor.UserValidateInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
	private final static  String securityKey="qfsdfsdfasd";
	
	@Autowired
	SEOHelper seoHelper;
	
	/**资源上传*/
	@Bean
	public ServletRegistrationBean<UploadServlet> servletRegistrationBean() {
		ServletRegistrationBean<UploadServlet> bean = new ServletRegistrationBean<UploadServlet>(new UploadServlet(), "/uploader");
		bean.setMultipartConfig(new MultipartConfigElement(""));
		return bean;
	}

	/**JSON数据处理*/
	@Bean
	MappingJackson2HttpMessageConverter httpMessageConverter() {
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();

		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(new MediaType("text", "json"));
		supportedMediaTypes.add(new MediaType("application", "json"));
		//设置中文编码格式
		supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);  
		jsonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);

		//JSON 处理方式
		ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.ALWAYS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")); 
		jsonMessageConverter.setObjectMapper(mapper);
		
		return jsonMessageConverter;
	}

    /**错误处理*/
    @Bean
    @Profile({"product"})
    public ErrorPageRegistrar errorPageRegistrar(){
    	return new ErrorPageRegistrar() {
			@Override
			public void registerErrorPages(ErrorPageRegistry registry) {
				registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/400"));
		    	registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
		    	registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/501"));
		    	registry.addErrorPages(new ErrorPage(Throwable.class, "/error/500"));
			}
		};
    }
    
    /**拦截器*/
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// 后台管理端拦劫器
		//registry.addInterceptor(new UserSecurityInterceptor("admin","loginUser")).addPathPatterns("/admin/**").excludePathPatterns("/admin/login/**");
		
		// 后台权限验证
		registry.addInterceptor(new AuthorizeInterceptor()).addPathPatterns("/admin/**").excludePathPatterns("/admin/login/**","/");
				
		//前台PC端拦截器
		registry.addInterceptor(new UserValidateInterceptor(PlatForm.PC)).addPathPatterns("/web/**");
				
		//前台手机端拦截器
	    registry.addInterceptor(new UserValidateInterceptor(PlatForm.Mobile)).addPathPatterns("/phone/**");
					
		//接口验证
		registry.addInterceptor(new SignInterceptor(securityKey)).addPathPatterns("/api/**");
	
		//StaticHtml
		registry.addInterceptor(new StaticHtmlInterceptor());
				
		//SEO
		registry.addInterceptor(new SEOInterceptor(seoHelper)).excludePathPatterns("/api/**","/error/**");
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
	/**静态资源地址映射*/
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String root = System.getProperty("user.dir");
		registry.addResourceHandler("/upload/**").addResourceLocations("file:" + root + "\\upload\\");
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}

	/**请求映射视图*/
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		
		registry.addRedirectViewController("/","/admin/index");
		
		WebMvcConfigurer.super.addViewControllers(registry);
	}
	
	/**请求路径配置*/
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		
		 //设置是否是后缀模式匹配，如“/user”是否匹配/user.*；
		configurer.setUseRegisteredSuffixPatternMatch(false);
		 //设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/"；
		configurer.setUseTrailingSlashMatch(true);
		
		UrlPathHelper pathHelper=new UrlPathHelper();
		pathHelper.setDefaultEncoding("UTF-8");
		configurer.setUrlPathHelper(pathHelper);
		
		WebMvcConfigurer.super.configurePathMatch(configurer);
	}
}
