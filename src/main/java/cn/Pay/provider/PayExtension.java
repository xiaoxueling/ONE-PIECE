package cn.Pay.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.Pay.service.PaymethodService;
import cn.core.setting.Setting;
import cn.util.DataConvert;
import cn.util.StringHelper;

@Service
public class PayExtension {
	
	@Autowired
	Setting setting;
	@Autowired
	PaymethodService paymethodService;
	
	public IPayProvider getPayProvider(String name){
		
		if(StringHelper.IsNullOrEmpty(name)){
			return null;
		}
		
		List<PayProviderEntity> list=PayProviderLoader.get();
		
		for(PayProviderEntity entity : list) {
			if(entity.getName().equalsIgnoreCase(name)){
				//return entity.getProvider();
				return ServiceHelper.getService(entity.getClassInfo());
			}
		}
		
		return null;
	}
	
	/**
	 * 支付
	 * @param param 参数
	 * @return
	 */
	@Transactional
	public PayResult Pay(PayParamEntity param){
		
		PayResult result=new PayResult(){
			{
				setSuccess(false);
				setError("没有对应的支付方式！");
			}
		};
		
		Map<String,Object> payMethodInfo=param.getPayMethodInfo();
		
		if(param.getPayMethodInfo()==null) {
			
			if(param.getPayMehtodId()<=0) {
				return result;
			}
			
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("id", param.getPayMehtodId());
			payMethodInfo = paymethodService.findByIdPayment(paramMap);
			if(payMethodInfo==null){
				return result;
			}
			param.setPayMethodInfo(payMethodInfo);
		}
		
		String payType=DataConvert.ToString(payMethodInfo.get("payType"));
		
		IPayProvider provider=getPayProvider(payType);
		if(provider==null){
			return result;
		}
		
		result=provider.Pay(param);
		
		result.setPayedMoney(param.getPayMoney());
		
		return result;
	}
}
