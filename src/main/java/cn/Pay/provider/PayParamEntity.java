package cn.Pay.provider;

import java.util.HashMap;
import java.util.Map;

import cn.Pay.provider.paycallback.IPayCallBack;
import cn.util.StringHelper;

public class PayParamEntity {
	
	
	public PayParamEntity(){
		payMethodInfo=new HashMap<String, Object>();
	}
	
	/**
	 * 订单Id
	 */
	private int orderId;
	
	/**
	 *  *订单号
	 */
	private String orderNo;
	
	/**
	 *  *付款金额
	 */
	private double payMoney;
	
	/**
	 * 订单名称（默认是orderNo）
	 */
	private String subject;
	
	/**
	 * 订单描述 （默认是orderNo）
	 */
	private String body;
	
	/**
	 * 支付方式Id
	 */
	private int payMehtodId;
	
	/**
	 * 支付方式信息
	 */
	private Map<String,Object> payMethodInfo;
	
	/**
	 * 回调方法
	 */
	private IPayCallBack callBack;
	
	
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}
	
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}

	public String getSubject() {
		if(StringHelper.IsNullOrEmpty(subject)){
			return this.orderNo;
		}
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		if(StringHelper.IsNullOrEmpty(subject)){
			return this.orderNo;
		}
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getPayMehtodId() {
		return payMehtodId;
	}

	public void setPayMehtodId(int payMehtodId) {
		this.payMehtodId = payMehtodId;
	}

	public Map<String, Object> getPayMethodInfo() {
		return payMethodInfo;
	}

	public void setPayMethodInfo(Map<String, Object> payMethodInfo) {
		this.payMethodInfo = payMethodInfo;
	}

	public IPayCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(IPayCallBack callBack) {
		this.callBack = callBack;
	}
}
