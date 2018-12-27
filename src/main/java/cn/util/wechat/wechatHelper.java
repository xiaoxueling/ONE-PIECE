package cn.util.wechat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hwpf.usermodel.Paragraph;
import org.json.JSONObject;

import cn.core.setting.model.WechatTemplateItem;
import cn.util.CalendarUntil;
import cn.util.DataConvert;
import cn.util.HttpClientUtils;
import cn.util.StringHelper;

import com.fasterxml.jackson.databind.ObjectMapper;


public class wechatHelper {

	private static String access_token="";
	//最新获取token 的时间
	private static Date lastGetokenDate=new Date();
	//token 有效期（秒）
	private static int tokenSecond=7100;
	
	//js sdk
	private static String jsapi_ticket="";
	private static Date lastGetTicketDate=new Date();
	private static int ticketSecond=7100;
	
	/**
	 * 获取微信推送的消息
	 * @param request
	 * @return
	 */
	public static WxMessage getWxMessage(HttpServletRequest request){
		
		WxMessage message=new WxMessage();
		
		try {
			 /** 读取接收到的xml消息 */  
	        StringBuffer sb = new StringBuffer();  
	        InputStream is = request.getInputStream();  
	        InputStreamReader isr = new InputStreamReader(is, "UTF-8");  
	        BufferedReader br = new BufferedReader(isr);  
	        String s = "";  
	        while ((s = br.readLine()) != null) {  
	            sb.append(s);  
	        } 
	        //此即为接收到微信端发送过来的xml数据 
	        String xml = sb.toString(); 
	        message=DataConvert.xmlToEntity(WxMessage.class, xml);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * 生成微信菜单
	 * @param menuJsonStr
	 * @param token
	 * @return
	 */
	public static Map<String, Object> createMenu(String menuJsonStr,String token){
		
		Map<String, Object> resultMap=new HashMap<String, Object>(){
			{
				put("result", false);
				put("msg", "更新微信公众号菜单失败!");
				put("code", "");
			}
		};
		
		if(StringHelper.IsNullOrEmpty(menuJsonStr)){
			resultMap.put("msg", "菜单数据不能为空");
			return resultMap;
		}
		
		String menuRequestUrl="https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+token;
		
		try{
			
			System.out.println(menuJsonStr);
			String result= HttpClientUtils.getInstance().httpPost(menuRequestUrl, menuJsonStr);
			
			ObjectMapper objMap=new ObjectMapper();
			Map map=objMap.readValue(result, HashMap.class);
			if(map!=null){
				
				String code=map.get("errcode")+"";
				String msg=map.get("errmsg")+"";
				
				if(code.equals("0")){
					resultMap.put("result", true);
					resultMap.put("msg", "更新微信公众号菜单成功!");
				}else{
					resultMap.put("code",code);
					resultMap.put("msg", "更新微信公众号菜单失败!("+msg+")");
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 生成微信用户验证请求地址
	 * @param url
	 * @param appId
	 * @return
	 */
	public static String creatWechatUrl(String url, String appId) {
		if(StringHelper.IsNullOrEmpty(url)){
			return "";
		}
		try {
			url=URLEncoder.encode(url, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String SCOPE = "snsapi_userinfo";//snsapi_base
		return "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+url+"&response_type=code&scope="+SCOPE+"&state=STATE#wechat_redirect";
	}
	
	/**
	 * 获取access_token
	 * @param appId
	 * @param appSecret
	 * @return
	 */
	public static String getAccess_token(String appId,String appSecret){
		Date now=new Date();
		Date reloadDate=CalendarUntil.Add(lastGetokenDate,Calendar.SECOND, tokenSecond);
		
		boolean flag=false;
		if(reloadDate.before(now)){
			flag=true;
		}
		if(StringHelper.IsNullOrEmpty(access_token)||flag){
			try {
				//获取token
				String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
				String result=HttpClientUtils.getInstance().httpGet(url);
				JSONObject jsn=new JSONObject(result);
				access_token=jsn.getString("access_token");
				lastGetokenDate=new Date();
				System.out.println("access_token:"+access_token+" time:"+CalendarUntil.ToDateString(lastGetokenDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return access_token;
	}
	
	/**
	 * 获取openId
	 * @param code
	 * @return
	 */
	public static String getOpenIdByCode(String code,String appId,String appSecret){
		String openId="";
		if(StringHelper.IsNullOrEmpty(code)||StringHelper.IsNullOrEmpty(appId)||StringHelper.IsNullOrEmpty(appSecret)){
			return openId;
		}
		String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appId+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
		
		try {
			String result= HttpClientUtils.getInstance().httpPost(url);
			JSONObject jsonObject=new JSONObject(result);
			openId=jsonObject.getString("openid");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return openId;
	}
	
	/**
	 * 根据openId 获取微信用户信息
	 * @param openId
	 * @param token
	 * @return
	 */
	public static WxUserInfo getUserInfo(String openId,String token){
		WxUserInfo userInfo= new WxUserInfo();
		
		if(StringHelper.IsNullOrEmpty(openId)){
			return userInfo;
		}
		
		String url="https://api.weixin.qq.com/cgi-bin/user/info?access_token="+token+"&openid="+openId+"&lang=zh_CN";
		
		try {
			
			String result= HttpClientUtils.getInstance().httpPost(url);
			
			JSONObject jObject=new JSONObject(result);
			
			userInfo.setNickname(getnewnick(jObject.getString("nickname")));
			userInfo.setOpenid(jObject.getString("openid"));
			userInfo.setCountry(jObject.getString("country"));
			userInfo.setProvince(jObject.getString("province"));
			userInfo.setCity(jObject.getString("city"));
			userInfo.setHeadimgurl(jObject.getString("headimgurl"));
			userInfo.setSubscribe(jObject.getInt("subscribe"));
			userInfo.setSex(jObject.getInt("sex")==1?"男":jObject.getInt("sex")==2?"女":"未知");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfo;
	}
	
	/**
	 * 生成带参数的二维码
	 * @param token
	 * @param id
	 * @return
	 */
	public static Map<String, Object> creatQrCode(String token,int id){
		Map<String, Object>resultMap=new HashMap<String, Object>();
		
		String url="https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+token;
		
		Map<String, Object>paraMap=new HashMap<String, Object>(){
			{
				put("expire_seconds", 60*60*24);
				put("action_name", "QR_SCENE");
				put("action_info", new HashMap<String, Object>(){
					{
						put("scene", new HashMap<String, Object>(){
							{
								put("scene_id", id);
							}
						});
					}
				});
			}
		};
		JSONObject jsonObject=new JSONObject(paraMap);
		try {
			String result=HttpClientUtils.getInstance().httpPost(url, jsonObject.toString());
			resultMap=DataConvert.JsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 * 获取用户增减数据 (最大时间跨度 7)
	 * @param token
	 * @param beginDate 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param endDate 获取数据的结束日期，end_date允许设置的最大值为昨日
	 * @return
	 */
	public static Map<String, Object>getUserSummary(String token,Date beginDate,Date endDate){
		
		Map<String, Object>resultMap=new HashMap<String, Object>();
		
		String url="https://api.weixin.qq.com/datacube/getusersummary?access_token="+token;
		
		Map<String, String>paraMap=new HashMap<String, String>(){
			{
				put("begin_date", CalendarUntil.ToDateString(beginDate,"yyyy-MM-dd"));
				put("end_date", CalendarUntil.ToDateString(endDate,"yyyy-MM-dd"));
			}
		};
		try {
			String result=HttpClientUtils.getInstance().httpPost(url, paraMap);
			resultMap=DataConvert.JsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 获取累计用户数据 (最大时间跨度 7)
	 * @param token
	 * @param beginDate 获取数据的起始日期，begin_date和end_date的差值需小于“最大时间跨度”（比如最大时间跨度为1时，begin_date和end_date的差值只能为0，才能小于1），否则会报错
	 * @param endDate 获取数据的结束日期，end_date允许设置的最大值为昨日
	 * @return
	 */
	public static Map<String, Object>getUserCumulate(String token,Date beginDate,Date endDate){
		
		Map<String, Object>resultMap=new HashMap<String, Object>();
		
		String url="https://api.weixin.qq.com/datacube/getusercumulate?access_token="+token;
		
		Map<String, String>paraMap=new HashMap<String, String>(){
			{
				put("begin_date", CalendarUntil.ToDateString(beginDate,"yyyy-MM-dd"));
				put("end_date", CalendarUntil.ToDateString(endDate,"yyyy-MM-dd"));
			}
		};
		try {
			String result=HttpClientUtils.getInstance().httpPost(url, paraMap);
			resultMap=DataConvert.JsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public static String getnewnick(String oldnick) {
		String regEx="[^0-9a-zA-Z\u4e00-\u9fa5]";   
		Pattern p = Pattern.compile(regEx);   
		Matcher m = p.matcher(oldnick); 
		String newnick = m.replaceAll("").trim();
		return newnick;
	}
	
	/**
	 * 文本消息
	 * @param ToUserName
	 * @param FromUserName
	 * @param MsgType
	 * @param Content
	 * @return
	 */
	public static String creatMessage(String ToUserName,String FromUserName,String MsgType,String Content){
		String  result="<xml>"
   		+ "<ToUserName><![CDATA["+ToUserName+"]]></ToUserName>"
   		+ "<FromUserName><![CDATA["+FromUserName+"]]></FromUserName>"
   		+ "<CreateTime>"+new Date().getTime()+"</CreateTime>"
   		+ "<MsgType><![CDATA["+MsgType+"]]></MsgType>";
		if(!MsgType.equalsIgnoreCase("transfer_customer_service")){
			result+="<Content><![CDATA["+Content+"]]></Content>";  		
		}
		result+="</xml>";
		return result;
	}
	
	/**
	 * 获取 jsapi_ticket
	 * @param accesstoken
	 * @return
	 */
	public static String getJsapi_ticket(String accesstoken){
		Date now=new Date();
		Date reloadDate=CalendarUntil.Add(lastGetTicketDate,Calendar.SECOND, ticketSecond);
		
		boolean flag=false;
		if(reloadDate.before(now)){
			flag=true;
		}
		if(StringHelper.IsNullOrEmpty(jsapi_ticket)||flag){
			try {
				//获取token
				String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accesstoken+"&type=jsapi";
				String result=HttpClientUtils.getInstance().httpGet(url);
				JSONObject jsn=new JSONObject(result);
				jsapi_ticket=jsn.getString("ticket");
				lastGetTicketDate=new Date();
				System.out.println("jsapi_ticket:"+jsapi_ticket+" time:"+CalendarUntil.ToDateString(lastGetTicketDate));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return jsapi_ticket;
	}
	
	/**
	 * JSSDK 签名
	 * @param jsapi_ticket
	 * @param url
	 * @return 各参数
	 */
	public static Map<String, String> sign(String jsapi_ticket, String url) {
        Map<String, String> ret = new HashMap<String, String>();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string = "jsapi_ticket=" + jsapi_ticket +
                  "&noncestr=" + nonce_str +
                  "&timestamp=" + timestamp +
                  "&url=" + url;
        try{
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string.getBytes("UTF-8"));
            signature = DataConvert.byteToStr(crypt.digest());
        }catch (Exception e){
            e.printStackTrace();
        }
        ret.put("url", url);
        ret.put("jsapi_ticket", jsapi_ticket);
        ret.put("nonceStr", nonce_str);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

	/**
	 * 时间戳
	 * @return
	 */
	public static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	/**
	 * 随机串
	 * @return
	 */
	public static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	/**
	 *  发送模板消息
	 * @param setting 模板消息配置数据
	 * @param data 要替换的数据
	 * @param url 模板跳转地址(可空)
	 * @param accesstoken token
	 * @param openId 接收者openid
	 * @param appId 商户公众号Id(url 为空时可不填)
	 * @return 成功返回的数据： 
	 *<pre>
	 *	"errcode":0,
     *	"errmsg":"ok",
     *	"msgid":200228332
	 * </pre>
	 */
	public static Map<String, Object> send_templateinfo(WechatTemplateItem setting,Map<String, Object>data,String url,String accesstoken,String openId,String appId){
		
		Map<String, Object>resultMap=new HashMap<String, Object>();
		
		if(setting==null){
			resultMap.put("errmsg", "set setting value needs!");
			return resultMap;
		}
		
		String postUrl="https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accesstoken;
		
		Map<String, Object>paraMap=new HashMap<String, Object>(){
			{
				put("touser", openId);
				put("template_id", setting.getTemplateId());
			}
		};
		if(!StringHelper.IsNullOrEmpty(url)&&!StringHelper.IsNullOrEmpty(appId)){
			paraMap.put("url", creatWechatUrl(url, appId));
		}
		
		Map<String, Object> dataMap=new HashMap<String, Object>(){
			{
				put("first", new HashMap<String, String>(){
					{
						put("value",repalce_mark(setting.getTitle(),data));
						put("color","#173177");
					}
				});
				for (Map.Entry<String, String> item : setting.getData().entrySet()) {
					
					put(item.getKey(), new HashMap<String, String>(){
						{
							put("value",repalce_mark(item.getValue(),data));
							put("color","#173177");
						}
					});
				}
				put("remark", new HashMap<String, String>(){
					{
						put("value",repalce_mark(setting.getRemark(),data));
						put("color","#173177");
					}
				});
			}
		};
		
		paraMap.put("data", dataMap);
		
		JSONObject jsonObject=new JSONObject(paraMap);
		try {
			String result=HttpClientUtils.getInstance().httpPost(postUrl, jsonObject.toString());
			resultMap=DataConvert.JsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 *模板标签替换
	 * @param temp
	 * @param dataMap
	 * @return
	 */
	public static String repalce_mark(String temp,Map<String, Object> dataMap){
		
		if(!StringHelper.IsNullOrEmpty(temp)&&dataMap!=null&&!dataMap.isEmpty()){
			
			String regEx=".*?\\{(?<key>.*?)\\}.*?";
			
			Pattern   pattern=Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);

			Matcher  matcher=pattern.matcher(temp);
			
			while (matcher.find()) {
				try {
					String key=matcher.group("key"); 
					if(!StringHelper.IsNullOrEmpty(key)){
						temp=temp.replaceAll("\\{"+key+"\\}",DataConvert.ToString(dataMap.get(key.trim())));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return temp;
	}
	
	public static void main(String[] args) {
		
		String temString="你好{ name},欢迎光临{store}";
		Map dataMap=new HashMap(){
			{
				put("name", "测试人员");
				put("store", "测试工厂站");
			}
		};
		
		String v=wechatHelper.repalce_mark(temString, dataMap);
		
		System.out.println(v);
	}
}
