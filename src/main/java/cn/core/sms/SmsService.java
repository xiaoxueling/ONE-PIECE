package cn.core.sms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.core.setting.Setting;
import cn.core.setting.model.SmsSetting;
import cn.core.sms.SmsSender.SmsState;
import cn.util.CalendarUntil;
import cn.util.DataConvert;
import cn.util.StringHelper;
import cn.util.Tools;


/**
 * 短信服务
 */
@Service
public class SmsService {
	
	private static final String SESSION_VERIFYCODE_KEY="SmsVerifyCode";
	private static final String SESSION_VERIFYPHONE="SmsVerifyPhone";
	private static final String SESSION_TIMEOUT_KEY="SmsTimeOut";
	
	/**
	 * APP 端短信验证缓存
	 */
	private static Map<String,Map<String,String>> APP_SMS_CACHE=new ConcurrentHashMap<String,Map<String,String>>();
	
	@Autowired
	Setting setting;
	
	/**
	 * 发送短信
	 * @param smsTemplateKey 短信模板名称
	 * @param expire 过期时间(秒)
	 * @param phone 手机号
	 * @param request HttpRequest
	 * @return true--发送成功  false--发送失败
	 */
	public boolean send(String smsTemplateKey,int expire,String phone,HttpServletRequest request){
		return this.send(smsTemplateKey, expire, phone, request, null,"",false);
	}
	
	/**
	 * 发送短信
	 * @param smsTemplateKey 短信模板名称
	 * @param expire 过期时间(秒)
	 * @param phone 手机号
	 * @param request HttpRequest
	 * @param data 模板数据
	 * @return true--发送成功  false--发送失败
	 */
	public boolean send(String smsTemplateKey,int expire,String phone,HttpServletRequest request,Map<String,String>data){
		return this.send(smsTemplateKey, expire, phone, request, data,"",false);
	}
	
	/**
	 * 发送短信(APP端)
	 * @param smsTemplateKey 短信模板名称
	 * @param expire 过期时间(秒)
	 * @param phone 手机号
	 * @param code 验证码
	 * @return true--发送成功  false--发送失败
	 */
	public boolean send(String smsTemplateKey,int expire,String phone,String code){
		return this.send(smsTemplateKey, expire, phone,null,null,code,true);
	}
	/**
	 * 发送短信(APP端)
	 * @param smsTemplateKey 短信模板名称
	 * @param expire 过期时间(秒)
	 * @param phone 手机号
	 * @param data 模板数据
	 * @param code 验证码
	 * @return true--发送成功  false--发送失败
	 */
	public boolean send(String smsTemplateKey,int expire,String phone,Map<String,String>data,String code){
		return this.send(smsTemplateKey, expire, phone,null, data,code,true);
	}
	
	/**
	 * 发送短信
	 * @param smsTemplateKey 短信模板名称
	 * @param expire 过期时间(秒)
	 * @param phone 手机号
	 * @param request HttpRequest
	 * @param data 模板数据
	 * @param code 验证码
	 * @param isApp 是否app端
	 * @return true--发送成功  false--发送失败
	 */
	public boolean send(String smsTemplateKey,int expire,String phone,HttpServletRequest request,Map<String,String>data,String code,boolean isApp){
		try {
			if(StringHelper.IsNullOrEmpty(code)){
				code=Tools.getOnlynumber(6);
			}
			Date now=new Date();
			if(!isApp){
				if(request==null){
					return false;
				}
				HttpSession session=request.getSession();
				//验证一定时间段内不可重复发送
				String sessionCode=DataConvert.ToString(session.getAttribute(SESSION_VERIFYCODE_KEY));
				if(!StringHelper.IsNullOrEmpty(sessionCode)){
					String timeOutString=DataConvert.ToString(session.getAttribute(SESSION_TIMEOUT_KEY));
					if(!StringHelper.IsNullOrEmpty(timeOutString)){
						Date timeOutDate=CalendarUntil.ParseDate(timeOutString, CalendarUntil.Add(now, Calendar.MINUTE, -10));
						if(timeOutDate.after(now)){
							return false;
						}
					}
				}
				session.setAttribute(SESSION_VERIFYCODE_KEY,code);
				session.setAttribute(SESSION_VERIFYPHONE, phone);
				session.setAttribute(SESSION_TIMEOUT_KEY, CalendarUntil.ToDateString(CalendarUntil.Add(now, Calendar.SECOND, expire)));
			}else{
				clearTimeOutCache();
				//根据短信模板类型来查找对应的手机号是否已经发送短信
				String timeOutString="";
				
				String key=smsTemplateKey+phone;
				
				if(APP_SMS_CACHE.containsKey(key)){
					timeOutString=APP_SMS_CACHE.get(key).get("timeOut");
				}
				
				if(!StringHelper.IsNullOrEmpty(timeOutString)){
					//进行时间判断
					Date timeOutDate=CalendarUntil.ParseDate(timeOutString, CalendarUntil.Add(now, Calendar.MINUTE, -10));
					if(timeOutDate.after(now)){
						return false;
					}
				}
				Map<String,String>cacheMap=new HashMap<String,String>();
				cacheMap.put("tempKey", smsTemplateKey);
				cacheMap.put("phone", phone);
				cacheMap.put("code", code);
				cacheMap.put("timeOut", CalendarUntil.ToDateString(CalendarUntil.Add(now, Calendar.SECOND, expire)));
				APP_SMS_CACHE.put(key,cacheMap);
			}
			
			if(data==null){
				data=new HashMap<String, String>();
			}
			data.put("code", code);
			
			SmsSetting smsSetting=setting.getSetting(SmsSetting.class);
			
			String template=smsSetting.getMessageSettings().get(smsTemplateKey);
			if(StringHelper.IsNullOrEmpty(template)){
				return false;
			}
			String message=this.parseTemplate(template, data);
			
			SmsState result=SmsSender.getInstance().send(smsSetting, phone, message);
			
			return result==SmsState.Ok;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 验证短信（APP端）
	 * @param smsTemplateKey 短信模板名称
	 * @param phone 手机号
	 * @param code 验证码
	 * @return
	 */
	public boolean Verify(String smsTemplateKey,String phone,String code){
		boolean flag=false;
		boolean isClear=false;
		String key=smsTemplateKey+phone;
		try {
			Date now=new Date();
			//根据短信模板类型来查找对应的手机号的验证码及过期时间
			String timeOutStr="";
			String cache_Code="";
			
			if(APP_SMS_CACHE.containsKey(key)){
				timeOutStr=APP_SMS_CACHE.get(key).get("timeOut");
				cache_Code=APP_SMS_CACHE.get(key).get("code");
			}
			
			//验证验证码是否相等
			if(StringHelper.IsNullOrEmpty(cache_Code)){
				return flag;
			}
			if(!cache_Code.trim().equalsIgnoreCase(code.trim())){
				return flag;
			}
			
			//验证是否过期
			if(!StringHelper.IsNullOrEmpty(timeOutStr)){
				Date timeOutDate=CalendarUntil.ParseDate(timeOutStr, CalendarUntil.Add(now, Calendar.MINUTE, -10));
				if(timeOutDate.after(now)){
					flag=true;
					isClear=true;
				}
				if(now.after(timeOutDate)){
					isClear=true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(isClear){
					APP_SMS_CACHE.remove(key);
				}
				clearTimeOutCache();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return flag;
	}
	
	/**
	 * 验证短信(web端)
	 * @param phone 手机号
	 * @param code 验证码
	 * @param request HttpRequest
	 * @return
	 */
	public boolean Verify(String phone,String code,HttpServletRequest request){
		boolean flag=false;
		boolean isClear=false;
		if(request==null){
			return flag;
		}
		HttpSession session=request.getSession();
		try {
			Date now=new Date();
			
			//验证手机号是否相等
			String sessionPhone=DataConvert.ToString(session.getAttribute(SESSION_VERIFYPHONE));
			if(StringHelper.IsNullOrEmpty(sessionPhone)) {
				return flag;
			}
			if(!sessionPhone.trim().equalsIgnoreCase(phone.trim())){
				return flag;
			}
			
			//验证验证码是否相等
			String sessionCode=DataConvert.ToString(session.getAttribute(SESSION_VERIFYCODE_KEY));
			if(StringHelper.IsNullOrEmpty(sessionCode)){
				return flag;
			}
			if(!sessionCode.trim().equalsIgnoreCase(code.trim())){
				return flag;
			}
			
			//验证是否过期
			String timeOutString=DataConvert.ToString(session.getAttribute(SESSION_TIMEOUT_KEY));
			if(!StringHelper.IsNullOrEmpty(timeOutString)){
				Date timeOutDate=CalendarUntil.ParseDate(timeOutString, CalendarUntil.Add(now, Calendar.MINUTE, -10));
				if(timeOutDate.after(now)){
					flag=true;
					isClear=true;
				}
				if(now.after(timeOutDate)){
					isClear=true;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(isClear){
				session.removeAttribute(SESSION_TIMEOUT_KEY);
				session.removeAttribute(SESSION_VERIFYCODE_KEY);
				session.removeAttribute(SESSION_VERIFYPHONE);
			}
		}
		return flag;
	}
	
	/**
	 * 转换模板数据
	 * @param template
	 * @param data
	 * @return
	 */
	private String parseTemplate(String template,Map<String, String>data){
		
		if (StringHelper.IsNullOrEmpty(template))
        {
           return "";
        }
        if (data==null || data.isEmpty())
        {
            return template;
        }
        for (Map.Entry<String, String> item: data.entrySet()) {
        	 template = template.replace(item.getKey(),item.getValue());
		}
        return template.trim();
	}
	
	/**
	 * 清除过期缓存数据
	 */
	private void clearTimeOutCache(){
		
		try{
			
			Date now =new Date();
			
			Iterator<Entry<String,Map<String,String>>> it = APP_SMS_CACHE.entrySet().iterator();
			
			while (it.hasNext()) {  
				Entry<String,Map<String,String>> entry =it.next();
				String key=entry.getKey();
				Map<String,String> value = entry.getValue();

				Date timeOutDate=CalendarUntil.ParseDate(value.get("timeOut"), CalendarUntil.Add(now, Calendar.MINUTE, -10));
				if(now.after(timeOutDate)){
					APP_SMS_CACHE.remove(key);
				}
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
