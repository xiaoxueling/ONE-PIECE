package cn.Pay.provider;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import cn.util.SpringContextUtils;

public class ServiceHelper {
	
	public  static <T>T getService(Class<T> clas) {
		ApplicationContext context = SpringContextUtils.getContext();
		return context.getBean(clas);
	}
}
