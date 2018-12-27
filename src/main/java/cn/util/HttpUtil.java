package cn.util;

import java.io.IOException;
import java.util.Map;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HttpUtil {
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @return
	 */
	public static String httpPost(String url) {
		return httpPost(url,null);
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @param paramsMap post 参数
	 * @return
	 */
	public static String httpPost(String url,Map<String, String>paramsMap) {
		return httpPost(url,null,paramsMap);
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @param headMap head参数
	 * @param paramsMap post 参数
	 * @return
	 */
    public static String httpPost(String url,Map<String, String>headMap,Map<String, String>paramsMap) { 
    	String result = "";
        CloseableHttpClient httpclient = HttpClients.createDefault(); 
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        
        //设置请求的HEAD 
        if(headMap!=null && !headMap.isEmpty()) {
        	for (Map.Entry<String, String> item : headMap.entrySet()) {
        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
        			httpPost.addHeader(item.getKey(), item.getValue());
        		}
			}
        }
        
        // 设置请求的参数
        if(paramsMap!=null && !paramsMap.isEmpty()) {
        	JSONObject jsonObject = new JSONObject(paramsMap);
        	 StringEntity params = new StringEntity(jsonObject.toString(),Consts.UTF_8);
             httpPost.setEntity(params);
        }
        
        try {  
            CloseableHttpResponse response = httpclient.execute(httpPost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    result = EntityUtils.toString(entity, "UTF-8");
                }  
            } finally {  
                response.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally { 
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return result;
    }	
}