package cn.Pay.provider.onlinepays.wechatpay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants.SignType;

import cn.Pay.provider.IOnlinePayProvider;
import cn.Pay.provider.OnlinePayNotifyResult;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;
import cn.Pay.provider.Pay;
import cn.Pay.provider.paycallback.IPayCallBack;
import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;
import cn.util.DataConvert;
import cn.util.StringHelper;



@Pay(name="微信扫码支付")
@Service
public class WechatProvider implements IOnlinePayProvider {
	@Autowired
	Setting setting;
	
	@Override
	public PayResult Pay(PayParamEntity param) {

		PayResult payResult=new PayResult(){
			{
				setOnline(true);
			}
		};
		
		try {
			
			SiteInfo siteInfo=setting.getSetting(SiteInfo.class);
			
			//商户号
			String mch_id=DataConvert.ToString(param.getPayMethodInfo().get("accountId"));
			if(StringHelper.IsNullOrEmpty(mch_id)){
				throw new Exception("账户（商户号）配置错误！");
			}
			String key="";
			String appId="";
			String ip="";
			try{
				String encryptionKey=DataConvert.ToString(param.getPayMethodInfo().get("encryptionKey"));
				String[] arr=encryptionKey.split("\\|");
				
				appId=arr[0];
				key=arr[1];
				ip=arr[2];
				
			}catch(Exception e){
				throw new Exception("密钥（APPID|KEY|IP）配置错误！");
			}
			
			WXPayConfigImpl config=new WXPayConfigImpl(appId, mch_id, key);
			
			WXPay wxPay=new WXPay(config,SignType.MD5);
			
			double totalMoney=param.getPayMoney();
			if(totalMoney<=0){
				throw new Exception("支付金额错误！");
			}
			
			double rate=DataConvert.ToDouble(param.getPayMethodInfo().get("rate"));
			
			if(rate>0){
				totalMoney+=totalMoney*rate/100;
			}
			int payMoney= new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue(); 
			
			HashMap<String, String> data = new HashMap<String, String>();
	        data.put("body", param.getBody());
	        data.put("out_trade_no",param.getOrderNo());
	        data.put("fee_type", "CNY");
	        data.put("total_fee", payMoney+"");
	        data.put("spbill_create_ip", ip);
	        data.put("notify_url", siteInfo.getSiteUrl()+"/pay/notify/wechatPay");
	        data.put("trade_type", "NATIVE");
	        data.put("product_id", param.getOrderId()+"");
	        
	        try {
	            Map<String, String> result = wxPay.unifiedOrder(data);
	            
	            //二维码地址
	            String code_url=result.get("code_url");
	            if(StringHelper.IsNullOrEmpty(code_url)){
	            	throw new Exception(result.get("err_code_des"));
	            }
	            
	            String  dataStr="<div id='wxpayqrcode' data-code='"+code_url+"'></div>"
	            		+"<script type='text/javascript' src='/manage/public/js/jquery.qrcode.min.js'></script>"
	            		+"<script type='text/javascript'>"
	            			+"$('#wxpayqrcode').qrcode($('#wxpayqrcode').data('code')); "
	            			+"setInterval(getOrderState,1000);"
	            			+"function getOrderState(){"
	            				+"$.get('/pay/payState',{"
	            					+ "orderNo:'" +param.getOrderNo() + "',"
	            					+ "r:Math.random()"
	            					+ "},function(json){"
	            						+ "if(json.result){"
	            							+ "window.location.href='/pay/result/wechatPay?orderNo=" + param.getOrderNo()+"';"
	            							+"}"
	            				+"},'json');"
	            			+"}"
	            		+"</script>";
	            
	            payResult.setSuccess(true);
	            payResult.setRequestData(dataStr);
	            
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            payResult.setError(e.getLocalizedMessage());
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			payResult.setError(e.getMessage());
		}
		finally{
			IPayCallBack callBack=param.getCallBack();
			if(callBack!=null){
				callBack.OrderPay(param, payResult);
			}
		}
		return payResult;
	}
	
	@Override
	public OnlinePayNotifyResult VerifyResult(PayParamEntity entity,Map<String, String> param) {
		return WechatCore.verifyPayResult(entity, param);
	}

	@Override
	public OnlinePayNotifyResult VerifyRefundResult(PayParamEntity entity,Map<String, String> inputParam) {
		// TODO Auto-generated method stub
		return null;
	}

}
