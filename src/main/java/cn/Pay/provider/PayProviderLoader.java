package cn.Pay.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.context.ApplicationContext;

import cn.Pay.provider.Pay.PayType;
import cn.util.SpringContextUtils;



public class PayProviderLoader {
	
	private static List<PayProviderEntity> providerEntities = new ArrayList<PayProviderEntity>();
	
	public static List<PayProviderEntity> get() {

		if (providerEntities.isEmpty()) {
			ApplicationContext context = SpringContextUtils.getContext();
			Map<String, Object> pays = context.getBeansWithAnnotation(Pay.class);

			Class<?> clazz = null;
			for (Map.Entry<String, Object> entry : pays.entrySet()) {
				clazz = entry.getValue().getClass();

				if (Advised.class.isAssignableFrom(clazz)) {
					Advised advised = (Advised) context.getBean(clazz);
					SingletonTargetSource singTarget = (SingletonTargetSource) advised.getTargetSource();
					clazz = singTarget.getTarget().getClass();
				}

				Pay pay = clazz.getAnnotation(Pay.class);
				if (pay == null) {
					continue;
				}
				String name=pay.name();
				boolean flag=false;
				for (PayProviderEntity entity : providerEntities) {
					if(entity.getName().equalsIgnoreCase(name)){
						flag=true;
						System.err.println(name+"重复!");
						break;
					}
				}
				if(flag){
					continue;
				}
				PayProviderEntity entity = new PayProviderEntity() {
					{
						setName(name);
						setOnline(pay.type() == PayType.Online);
					}
				};

				try {
					entity.setClassInfo((Class<? extends IPayProvider>) clazz);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				providerEntities.add(entity);
			}
		}

		return providerEntities;
	}

}
