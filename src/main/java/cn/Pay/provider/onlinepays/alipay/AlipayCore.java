package cn.Pay.provider.onlinepays.alipay;

import java.math.BigDecimal;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

import cn.Pay.provider.OnlinePayNotifyResult;
import cn.Pay.provider.PayParamEntity;
import cn.util.DataConvert;
import cn.util.StringHelper;

public class AlipayCore {

	/**
	 * 比对 交易数据是否正确
	 * @param entity
	 * @param param
	 * @return
	 */
	public static boolean checkParam(PayParamEntity entity,Map<String, String> param){
		
		/* 添加以下校验：
		1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		2、判断total_amount是否确实为该订单的实际金额,
		3、验证app_id是否为该商户本身。
		*/
		
		String orderNo=param.get("out_trade_no");
		if(!orderNo.equals(entity.getOrderNo())){
			return false;
		}
		double totalMoney=entity.getPayMoney();
		double rate=DataConvert.ToDouble(entity.getPayMethodInfo().get("rate"));
		if(rate>0){
			totalMoney+=totalMoney*rate/100;
		}
		totalMoney= new BigDecimal(totalMoney).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
		
		if(totalMoney!=DataConvert.ToDouble(param.get("total_amount"))){
			return false;
		}
		
		String appId=DataConvert.ToString(entity.getPayMethodInfo().get("accountId"));
		
		if(!appId.equals(param.get("app_id"))){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 支付验证
	 * @param entity
	 * @param param
	 * @return
	 */
	public static OnlinePayNotifyResult verifPayResult(PayParamEntity entity,Map<String, String> param){
		OnlinePayNotifyResult result=new OnlinePayNotifyResult();
		
		try {
			
			boolean flag=AlipayCore.checkParam(entity, param);
			if(!flag){
				throw new Exception("参数错误！");
			}
			
			String appId=DataConvert.ToString(entity.getPayMethodInfo().get("accountId"));
			if(StringHelper.IsNullOrEmpty(appId)){
				throw new Exception("账户（appId）配置错误！");
			}
			String privateKey="";
			String publicKey="";
			
			try{
				String encryptionKey=DataConvert.ToString(entity.getPayMethodInfo().get("encryptionKey"));
				String[] arr=encryptionKey.split("\\|");
				
				privateKey=arr[0];
				publicKey=arr[1];
				
			}catch(Exception e){
				throw new Exception("密钥(privateKey|publicKey)配置错误！");
			}
			
			boolean signVerified=false;
			try {
				 signVerified = AlipaySignature.rsaCheckV1(param,publicKey,"utf-8","RSA2");
			} catch (AlipayApiException e) {
				e.printStackTrace();
				throw new Exception("验签失败！");
			}
			if(!signVerified){
				throw new Exception("验签失败！");
			}

			String tradeNo = param.get("trade_no");

			// 获得初始化的AlipayClient
			AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", appId, privateKey,"json", "utf-8", publicKey, "RSA2");

			// 设置请求参数
			AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

			AlipayTradeQueryModel model = new AlipayTradeQueryModel();
			model.setOutTradeNo(entity.getOrderNo());
			model.setTradeNo(tradeNo);
			alipayRequest.setBizModel(model);

			AlipayTradeQueryResponse response = alipayClient.execute(alipayRequest);

			if (response.getCode().equals("10000") && response.getTradeStatus()
					.equalsIgnoreCase("TRADE_SUCCESS")) {
				result.setSuccess(true);
				result.setTradeNo(response.getTradeNo());
				result.setMessage("success");
			}

		} catch (Exception e) {
			result.setError(e.getMessage());
			result.setMessage(e.getMessage());
		}
		return result;
	}
}
