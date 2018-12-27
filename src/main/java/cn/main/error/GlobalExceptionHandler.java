package cn.main.error;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 统一异常处理
 *
 */
@ControllerAdvice
@Profile({"product"})
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ModelAndView exceptionHandler(Exception e){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("exception", e);
		return new ModelAndView("/error/exception",map);
	}
}
