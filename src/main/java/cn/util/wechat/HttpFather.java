package cn.util.wechat;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

public class HttpFather {
	/**
	 * 获得秘钥库 KeyStore
	 * @param passWord 密码
	 * @param keyStorePath 秘钥库文件路径
	 * @return 秘钥库
	 * @throws Exception
	 * 
	 */
	public static String pre_SSL="SSL";
	public  KeyStore getStore(String type,String passWord,String keyStorePath) throws Exception{
		//获得秘钥库
		KeyStore ks=KeyStore.getInstance(type);
		//获得秘钥库文件流
		FileInputStream is=new FileInputStream(keyStorePath);
		//加载秘钥库
		ks.load(is, passWord.toCharArray());
		//关闭秘钥库文件流
		is.close();
		return ks;
	}
	
	public  CloseableHttpClient getclient(String type,String passWord,String keyStorePath) throws Exception{
		//获得秘钥库
		KeyStore keystore=getStore(type,passWord, keyStorePath);
		 // 相信自己的CA和所有自签名的证书  
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keystore, passWord.toCharArray()).build();
		// 只允许是用TLSv1协议
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build(); 
        return httpclient;
	}
	
	public  String sendPost(String secType,String url,Map<String,Object> param,String type,String passWord,String keyStorePath){
		String resule="";
		//创建httpClient
		CloseableHttpClient httpClient = null;
		//创建参数队列 最后将成为键值对序列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
		Set set = param.keySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) param.get(key);
            if(value!=null && !value.equals("")){
            	formparams.add(new BasicNameValuePair(key, value));
            }else{
            	continue;
            }
        }
		HttpEntity entity;
		try {
			if(secType!=null && secType.contains("SSL")){
				httpClient = getclient(type,passWord,keyStorePath);
        	}else{
        		httpClient=HttpClients.createDefault();
        	}
			//创建httpPost对象
			HttpPost httpPost=new HttpPost(url);
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(entity);
			CloseableHttpResponse response=httpClient.execute(httpPost);
			try{
				HttpEntity resEntity=response.getEntity();
				resule=EntityUtils.toString(resEntity,"UTF-8"); 
			}finally{
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(httpClient!=null)
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resule;
	}
	
	
	
	 /** 
     * 发送 get请求 
     */  
    public  String get(String secType,String url,String param,String type,String passWord,String keyStorePath) {  
    	String result="";
        CloseableHttpClient httpclient = null;
        try {  
            // 创建httpget.   
        	if(secType!=null && secType.contains("SSL")){
        		httpclient = getclient(type,passWord,keyStorePath);
        	}else{
            	httpclient=HttpClients.createDefault();
        	}
            HttpGet httpget = new HttpGet(url);  
            // 执行get请求    
            CloseableHttpResponse response = null;  
            try {  
            	response = httpclient.execute(httpget); 
                // 获取响应实体    
                HttpEntity entity = response.getEntity();  
                //System.out.println(response.getStatusLine());  
                if (entity != null) {  
                	//读取响应
                	result=EntityUtils.toString(entity,"UTF-8"); 
                }  
            } finally {  
                response.close();  
            }  
        } catch (Exception e){
        	e.printStackTrace();
        } finally {  
            // 关闭连接,释放资源    
            try {  
            	if(httpclient!=null)
            		httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return result;
    }  
}
