package cn.core.sms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.core.setting.model.SmsSetting;
import cn.util.StringHelper;



public class SmsSender {

	
	private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

	private static SmsSender sender=null;
	private static HttpClient client = null;
	
	public SmsSender(){
		InitHttpClient();
	}
	
	/**
	 * 初始化client
	 */
	private void InitHttpClient(){
		MultiThreadedHttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		// 默认连接超时时间
		params.setConnectionTimeout(60000);
		// 默认读取超时时间
		params.setSoTimeout(60000);
		// 默认单个host最大连接数
		params.setDefaultMaxConnectionsPerHost(200);
		// 最大总连接数
		params.setMaxTotalConnections(500);
		//重试次数，默认是3次；当前是禁用掉
		params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
		
		httpConnectionManager.setParams(params);

		client = new HttpClient(httpConnectionManager);
	}
	
	/**
	 * 获取sender实例
	 * @return
	 */
	public static SmsSender getInstance(){
		if(sender==null){
			sender=new SmsSender();
		}
		return sender;
	}
	
	/**
	 * 发送短信
	 * @param setting 短信配置
	 * @param phone 手机号
	 * @param message 信息
	 * @return
	 */
	public SmsState send(SmsSetting setting,String phone,String message){
		return this.send(setting, phone, message, 5);
	}
	
	/**
	 * 发送短信
	 * @param setting 短信配置
	 * @param phone 手机号
	 * @param message 信息
	 * @param priority 优先级
	 * @return
	 */
	public SmsState send(SmsSetting setting,String phone,String message,int priority){
		
		if(setting==null){
			return SmsState.NoSetting;
		}
		if(StringHelper.IsNullOrEmpty(phone)){
			return SmsState.NoTelephoneNumber;
		}
		if(StringHelper.IsNullOrEmpty(message)){
			return SmsState.NoContent;
		}
		if(priority<=0){
			priority=5;
		}
		try {
			//请求的地址
			String baseUrl=setting.getServer();
			if(StringHelper.IsNullOrEmpty(baseUrl)){
				return SmsState.NoSetting;
			}
			//请求的参数
			String requestParam="?cdkey="+URLEncoder.encode(setting.getAccount(), "UTF-8");
			requestParam+="&password="+URLEncoder.encode(setting.getPassword(), "UTF-8");
			requestParam+="&phone="+URLEncoder.encode(phone, "UTF-8");
			requestParam+="&message="+URLEncoder.encode(message, "UTF-8");
			requestParam+="&seqid="+setting.getServiceNumber();
			requestParam+="&smspriority="+priority;
			
			HttpMethod method = new GetMethod(baseUrl+requestParam);
			
			try {
				log.info("【短信发送】"+baseUrl+requestParam);
			} catch (Exception e) {
			}
			
			try {
				int statusCode = client.executeMethod(method);
				InputStream _InputStream = null;
				if (statusCode == HttpStatus.SC_OK) {
					_InputStream = method.getResponseBodyAsStream();
				}
				if (_InputStream != null) {
					return getResponseState(_InputStream);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return SmsState.ServerNotFind;
			}
			finally {
				method.releaseConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SmsState.None;
	}
	
	/**
	 * 获取返回的状态
	 * @return
	 */
	private SmsState getResponseState(InputStream stream){
		String response="";
		try {
			if (stream != null) {
				StringBuffer buffer = new StringBuffer();
				InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
				Reader in = new BufferedReader(isr);
				int ch;
				while ((ch = in.read()) > -1) {
					buffer.append((char) ch);
				}
				response = buffer.toString();
				buffer = null;
			}
			if (StringHelper.IsNullOrEmpty(response))
            {
                return SmsState.SendFailure;
            }
			
			Document document = null;
			document = DocumentHelper.parseText(response);
			Element root = document.getRootElement();
			Element errorElement=root.element("error");
			if(errorElement!=null){
				
				String error=errorElement.getTextTrim();
				switch (error) {
					case "0":
						return SmsState.Ok;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return SmsState.None;
	}

	/**
	 * 短信发送状态
	 */
	public enum SmsState {
		None,
		/**
		 * 成功
		 */
		Ok,
		SendFailure,
        NoContent,
        NoTelephoneNumber,
        NoSetting,
        UserNameOrPasswordError,
        TimeOut,
        ServerNotFind
	}
}
