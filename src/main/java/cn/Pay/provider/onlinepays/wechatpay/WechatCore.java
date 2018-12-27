package cn.Pay.provider.onlinepays.wechatpay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayConstants.SignType;

import cn.Pay.provider.OnlinePayNotifyResult;
import cn.Pay.provider.PayParamEntity;
import cn.util.DataConvert;
import cn.util.StringHelper;

public class WechatCore {

	public static OnlinePayNotifyResult verifyPayResult(PayParamEntity entity,Map<String, String> param) {
		
		OnlinePayNotifyResult result=new OnlinePayNotifyResult();
		
		try {
			
			if(!entity.getOrderNo().equalsIgnoreCase(param.get("out_trade_no"))){
				throw new Exception("订单号验证失败！");
			}
			
			//商户号
			String mch_id=DataConvert.ToString(entity.getPayMethodInfo().get("accountId"));
			if(StringHelper.IsNullOrEmpty(mch_id)){
				throw new Exception("账户（商户号）配置错误！");
			}
			
			if(!mch_id.equals(param.get("mch_id"))){
				throw new Exception("商户号验证失败！");
			}
			
			double totalMoney=entity.getPayMoney();
			if(totalMoney<=0){
				throw new Exception("支付金额错误！");
			}
			
			double rate=DataConvert.ToDouble(entity.getPayMethodInfo().get("rate"));
			
			if(rate>0){
				totalMoney+=totalMoney*rate/100;
			}
			int payMoney= new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue(); 
			
			if(payMoney!=DataConvert.ToInteger(param.get("total_fee"))){
				throw new Exception("支付金额验证失败！");
			}
			
			String key="";
			String appId="";
			String ip="";
			try{
				String encryptionKey=DataConvert.ToString(entity.getPayMethodInfo().get("encryptionKey"));
				String[] arr=encryptionKey.split("\\|");
				
				appId=arr[0];
				key=arr[1];
				ip=arr[2];
				
			}catch(Exception e){
				throw new Exception("密钥（APPID|KEY|IP）配置错误！");
			}
			
			if(!appId.equals(param.get("appid"))){
				throw new Exception("appid验证失败！");
			}
			
			WXPayConfigImpl config=new WXPayConfigImpl(appId, mch_id, key);
			
			WXPay wxPay=new WXPay(config,SignType.MD5);
			
			boolean signVerified=wxPay.isPayResultNotifySignatureValid(param);
			if(!signVerified){
				throw new Exception("验签失败！");
			}
			
			if(param.get("return_code").equalsIgnoreCase("SUCCESS")&&param.get("result_code").equalsIgnoreCase("SUCCESS")){
				String tradeNo=DataConvert.ToString(param.get("transaction_id"));
				
				HashMap<String, String> data = new HashMap<String, String>();
			    data.put("out_trade_no",entity.getOrderNo());
			    data.put("transaction_id", tradeNo);
			    
			    Map<String, String> responseMap=wxPay.orderQuery(data);
			    
			    if(responseMap.get("return_code").equalsIgnoreCase(WXPayConstants.SUCCESS)
			    		&&responseMap.get("result_code").equalsIgnoreCase(WXPayConstants.SUCCESS)
			    		&&responseMap.get("trade_state").equalsIgnoreCase(WXPayConstants.SUCCESS)){
			    	
			    	result.setSuccess(true);
			    	result.setTradeNo(tradeNo);
			    	result.setMessage("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setError(e.getMessage());
			result.setMessage("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA["+e.getMessage()+"]]></return_msg></xml>");
		}
		
		return result;
		
	}
}
