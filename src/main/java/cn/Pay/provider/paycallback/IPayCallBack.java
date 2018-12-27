package cn.Pay.provider.paycallback;

import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;

/**
 * 支付回调接口
 * @author xiaoxueling
 *
 */
public interface IPayCallBack {

	/**
	 * 订单支付回调 
	 * @param paramEntity 请求的数据
	 * @param payResult 支付结果
	 */
	public void OrderPay(PayParamEntity paramEntity,PayResult payResult);
	
	/**
	 * 订单退款回调 
	 * @param paramEntity 请求的数据
	 * @param payResult 支付结果
	 */
	public void OrderRefund(PayParamEntity paramEntity,PayResult payResult);
	
}
