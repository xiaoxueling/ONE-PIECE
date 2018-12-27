package cn.core.setting.model;

import java.util.HashMap;
import java.util.Map;

/**
 *微信模板消息配置
 *@author xiaoxueling
 */
public class WechatTemplateSetting {
	
	public WechatTemplateSetting(){
		items=new HashMap<String,WechatTemplateItem>();
	}

	//预约成功
	private boolean register_success;
	//预约取消
	private boolean register_cancel;
	//预约取件中
	private boolean register_sending;
	
	//订单生成成功（预约取件成功）
	private boolean order_success;
	//订单取消
	private boolean order_cancel;
	//订单退款
	private boolean order_refund;
	//衣物清洗中
	private boolean order_washing;
	//衣物已反店
	private boolean order_backstore;
	//衣物配送中
	private boolean order_sending;
	//订单完成（用户取件）
	private boolean order_complete;
	
	//账户购买会员卡成功
	private boolean purse_buycard;
	//账户退款
	private boolean purse_refund;
	
	//模板数据
	private Map<String,WechatTemplateItem> items;
	
	
	
	public boolean isRegister_success() {
		return register_success;
	}

	public void setRegister_success(boolean register_success) {
		this.register_success = register_success;
	}

	public boolean isRegister_cancel() {
		return register_cancel;
	}

	public void setRegister_cancel(boolean register_cancel) {
		this.register_cancel = register_cancel;
	}

	public boolean isRegister_sending() {
		return register_sending;
	}

	public void setRegister_sending(boolean register_sending) {
		this.register_sending = register_sending;
	}

	public boolean isOrder_success() {
		return order_success;
	}

	public void setOrder_success(boolean order_success) {
		this.order_success = order_success;
	}

	public boolean isOrder_cancel() {
		return order_cancel;
	}

	public void setOrder_cancel(boolean order_cancel) {
		this.order_cancel = order_cancel;
	}

	public boolean isOrder_refund() {
		return order_refund;
	}

	public void setOrder_refund(boolean order_refund) {
		this.order_refund = order_refund;
	}

	public boolean isOrder_washing() {
		return order_washing;
	}

	public void setOrder_washing(boolean order_washing) {
		this.order_washing = order_washing;
	}

	public boolean isOrder_backstore() {
		return order_backstore;
	}

	public void setOrder_backstore(boolean order_backstore) {
		this.order_backstore = order_backstore;
	}

	public boolean isOrder_sending() {
		return order_sending;
	}

	public void setOrder_sending(boolean order_sending) {
		this.order_sending = order_sending;
	}

	public boolean isOrder_complete() {
		return order_complete;
	}

	public void setOrder_complete(boolean order_complete) {
		this.order_complete = order_complete;
	}

	public boolean isPurse_buycard() {
		return purse_buycard;
	}

	public void setPurse_buycard(boolean purse_buycard) {
		this.purse_buycard = purse_buycard;
	}

	public boolean isPurse_refund() {
		return purse_refund;
	}

	public void setPurse_refund(boolean purse_refund) {
		this.purse_refund = purse_refund;
	}

	public Map<String, WechatTemplateItem> getItems() {
		return items;
	}

	public void setItems(Map<String, WechatTemplateItem> items) {
		this.items = items;
	}
}
