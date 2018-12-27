package cn.core.setting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SmsSetting {

	//服务器地址
	private String server;
	//账户
	private String account;
	//密码
	private String password;
	//特服号
	private String serviceNumber;
	//短信模板配置
	public Map<String,String> messageSettings;
	
	public SmsSetting(){
		messageSettings=new LinkedHashMap<String, String>();
	}
	
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getServiceNumber() {
		return serviceNumber;
	}
	public void setServiceNumber(String serviceNumber) {
		this.serviceNumber = serviceNumber;
	}
	public Map<String, String> getMessageSettings() {
		return messageSettings;
	}
	public void setMessageSettings(Map<String, String> messageSettings) {
		this.messageSettings = messageSettings;
	}
	
	public void InitMessage(List<String>keys){
		try {
			Map<String,String> message=this.getMessageSettings();
			for (String key : keys) {
	      		if(!message.containsKey(key)){
	      			message.put(key,"");
	      		}
			}
	      	for (String key : message.keySet()) {
	      		if(!keys.contains(key)){
	      			message.remove(key);
	      		}
			}
	      	this.setMessageSettings(this.getMessageSettings());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Map<String, String>> getMessageList(){
		List<Map<String, String>> msgList=new ArrayList<Map<String,String>>();
      	for (Map.Entry<String, String> item : this.getMessageSettings().entrySet()) {
			String key=item.getKey();
			String value=item.getValue();
			
			msgList.add(new HashMap<String, String>(){
				{
					put("name", key);
					put("messge", value);
				}
			});
		}
      	return msgList;
	}
}
