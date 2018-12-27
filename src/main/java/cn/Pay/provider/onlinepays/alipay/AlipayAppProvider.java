package cn.Pay.provider.onlinepays.alipay;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;

import cn.Pay.provider.IOnlinePayProvider;
import cn.Pay.provider.OnlinePayNotifyResult;
import cn.Pay.provider.Pay;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;
import cn.Pay.provider.paycallback.IPayCallBack;
import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;
import cn.util.DataConvert;
import cn.util.StringHelper;

@Pay(name="APP支付宝即时到账")
@Service
public class AlipayAppProvider implements IOnlinePayProvider {

	@Autowired
	Setting setting;
	
	@Override
	public PayResult Pay(PayParamEntity param) {
		
		PayResult payResult=new PayResult(){
			{
				setSuccess(false);
				setOnline(true);
			}
		};
		
		try {
			
			SiteInfo siteInfo=setting.getSetting(SiteInfo.class);
			
			String appId=DataConvert.ToString(param.getPayMethodInfo().get("accountId"));
			if(StringHelper.IsNullOrEmpty(appId)){
				payResult.setError("账户配置错误！");
				return payResult;
			}
			String privateKey="";
			String publicKey="";
			
			try{
				String encryptionKey=DataConvert.ToString(param.getPayMethodInfo().get("encryptionKey"));
				String[] arr=encryptionKey.split("\\|");
				
				privateKey=arr[0];
				publicKey=arr[1];
				
			}catch(Exception e){
				payResult.setError("密钥配置错误！");
				return payResult;
			}
			
			double totalMoney=param.getPayMoney();
			if(totalMoney<=0){
				payResult.setError("支付金额错误！");
				return payResult;
			}
			
			double rate=DataConvert.ToDouble(param.getPayMethodInfo().get("rate"));
			
			if(rate>0){
				totalMoney+=totalMoney*rate/100;
			}
			totalMoney= new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			
			//获得初始化的AlipayClient
			AlipayClient alipayClient=new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId,privateKey,"json","utf-8",publicKey,"RSA2");
			
			//设置请求参数
			AlipayTradeAppPayRequest alipayRequest=new AlipayTradeAppPayRequest();
			alipayRequest.setNotifyUrl(siteInfo.getSiteUrl()+"/pay/notify/alipayAppPay");
			
			AlipayTradeAppPayModel model=new AlipayTradeAppPayModel();
			
			model.setOutTradeNo(param.getOrderNo());
			model.setProductCode("QUICK_MSECURITY_PAY");
			model.setTotalAmount(totalMoney+"");
			model.setSubject(param.getSubject());
			model.setBody(param.getBody());
			
			alipayRequest.setBizModel(model);
			
			//获取请求
			String result= alipayClient.sdkExecute(alipayRequest).getBody();
			
			payResult.setSuccess(true);
			payResult.setError("");
			payResult.setRequestData(result);
			
			return payResult;

		} catch (Exception e) {
			e.printStackTrace();
			payResult.setError(e.getMessage());
			return payResult;
		}
		finally{
			IPayCallBack callBack=param.getCallBack();
			if(callBack!=null){
				callBack.OrderPay(param, payResult);
			}
		}
	}

	@Override
	public OnlinePayNotifyResult VerifyResult(PayParamEntity entity,Map<String, String> param) {
		return AlipayCore.verifPayResult(entity, param);
	}

	@Override
	public OnlinePayNotifyResult VerifyRefundResult(PayParamEntity entity,Map<String, String> param) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
