package cn.core.authorize;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import cn.util.SpringContextUtils;
import cn.util.StringHelper;

public class AuthorizeSettingLoader {

	private static Map<String, List<String>> map;

	public static Map<String, List<String>> get() {
		if (map == null) {
			map = new HashMap<String, List<String>>();
			ApplicationContext context = SpringContextUtils.getContext();
			Map<String, Object> controllers = context.getBeansWithAnnotation(Controller.class);
			Class<? extends Object> clazz = null;
			for (Map.Entry<String, Object> entry : controllers.entrySet()) {
				clazz = entry.getValue().getClass();
				if (Advised.class.isAssignableFrom(clazz)) {
					Advised advised = (Advised) context.getBean(clazz);
					SingletonTargetSource singTarget = (SingletonTargetSource) advised.getTargetSource();
					clazz = singTarget.getTarget().getClass();
				}
				Authorize authorize = clazz.getAnnotation(Authorize.class);
				setValues(authorize);
				Method[] methods = clazz.getDeclaredMethods();
				for (Method m : methods) {
					Authorize mAuthorize = m.getAnnotation(Authorize.class);
					setValues(mAuthorize);
				}
			}
		}
		return map;
	}

	private static void setValues(Authorize authorize) {
		if (authorize == null) {
			return;
		}
		String setting = authorize.setting();
		String[] arr = setting.split(",");
		if(arr!=null&&arr.length>0) {
			for (String item : arr) {
				String[] resourceAction=item.split("-");
				if(resourceAction==null||resourceAction.length!=2) {
					continue;
				}
				String menu=resourceAction[0];
				String action=resourceAction[1];
				if(StringHelper.IsNullOrEmpty(menu)||StringHelper.IsNullOrEmpty(action)) {
					continue;
				}
				if(map.containsKey(menu)) {
					if(!map.get(menu).contains(action)) {
						map.get(menu).add(action);
					}
				}else {
					map.put(menu, new ArrayList<String>() {
						{
							add(action);
						}
					});
				}
			}
		}
	}
}
