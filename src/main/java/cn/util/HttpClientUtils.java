package cn.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * httpClient工具类
 * @author XIAOXUELING
 *
 */
public class HttpClientUtils {
	
	private static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

	private static final String DEFAULT_KEYSTORE_TYPE="SSL";
	
	/**
	 * 单例对象
	 */
	private static HttpClientUtils httpClientUtils;
	
	/**
	 * 下载文件线程池服务
	 */
	private ExecutorService downloadExcutorService;
	
	/**
	 * 最大线程池
	 */
	public static final int THREAD_POOL_SIZE=5;
	
	/**
	 * 下载文件进度回调
	 */
	public interface HttpClientDownLoadProgress{
		public void onProgress(int progress);
	}
	
	private HttpClientUtils() {
		downloadExcutorService=Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}
	
	/**
	 * 获取实例
	 */
	public static HttpClientUtils getInstance() {
		if(httpClientUtils==null) {
			httpClientUtils = new HttpClientUtils();
		}
		return httpClientUtils;
	}
	
	/**
	 * get 请求
	 * @param url 请求地址
	 */
	public String httpGet(String url) {
		return httpGet(url, null);
	}
	
	/**
	 * get 请求
	 * @param url 请求地址
	 * @param headMap  head参数
	 * @return
	 */
	public String httpGet(String url,Map<String,String>headMap) {
		String result="";
		CloseableHttpClient httpClient=HttpClients.createDefault();
		try {
			
			HttpGet httpGet=new HttpGet(url);
			
			//设置请求的HEAD 
	        if(headMap!=null && !headMap.isEmpty()) {
	        	for (Map.Entry<String, String> item : headMap.entrySet()) {
	        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
	        			httpGet.addHeader(item.getKey(), item.getValue());
	        		}
				}
	        }
	        
	        CloseableHttpResponse response = httpClient.execute(httpGet);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    result = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                }  
            } finally {  
                response.close();  
            }
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}finally { 
            try {  
            	if(httpClient!=null) {
					httpClient.close();
				}  
            } catch (IOException e) {  
            	log.error(e.getMessage()); 
            }
        }  
		return result;
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @return
	 */
	public String httpPost(String url) {
		return httpPost(url,"");
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @param paramsMap post 参数
	 * @return
	 */
	public String httpPost(String url,Map<String, String>paramsMap) {
		return httpPost(url,null,paramsMap);
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @param params post 参数
	 * @return
	 */
	public String httpPost(String url,String params) {
		return httpPost(url,null,params);
	}
	
	/**
	 * post 请求
	 * @param url 请求地址
	 * @param headMap head参数
	 * @param paramsMap post 参数
	 * @return
	 */
    public String httpPost(String url,Map<String, String>headMap,Map<String, String>paramsMap) { 
    	String params="";
        if(paramsMap!=null && !paramsMap.isEmpty()) {
        	JSONObject jsonObject = new JSONObject(paramsMap);
        	params=jsonObject.toString();
        }
        return httpPost(url, headMap, params);
    }
    
    /**
	 * post 请求
	 * @param url 请求地址
	 * @param headMap head参数
	 * @param params post 参数
	 * @return
	 */
    public String httpPost(String url,Map<String, String>headMap,String params) { 
    	String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault(); 
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "charset=utf-8");
        
        //设置请求的HEAD 
        if(headMap!=null && !headMap.isEmpty()) {
        	for (Map.Entry<String, String> item : headMap.entrySet()) {
        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
        			httpPost.addHeader(item.getKey(), item.getValue());
        		}
			}
        }
        
        // 设置请求的参数
        if(!StringHelper.IsNullOrEmpty(params)) {
        	StringEntity paramsEntity = new StringEntity(params,Consts.UTF_8);
            httpPost.setEntity(paramsEntity);
        }
        
        try {  
            CloseableHttpResponse response = httpClient.execute(httpPost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    result = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                }  
            } finally {  
                response.close();  
            }  
        } catch (Exception e) {  
        	log.error(e.getMessage()); 
        } finally { 
            try {  
            	if(httpClient!=null) {
					httpClient.close();
				}  
            } catch (IOException e) {  
            	log.error(e.getMessage());
            }  
        }  
        return result;
    }
    
    /**
	 * 下载文件
	 * 
     * @param url 请求地址
     * @param filePath 文件保存的路径
	 */
	public void httpDownloadFile(final String url, final String filePath) {
		downloadExcutorService.execute(new Runnable() {
 
			@Override
			public void run() {
				httpDownloadFile(url, filePath, null, null);
			}
		});
	}
 
    /**
     * 下载文件
     * @param url 请求地址
     * @param filePath 文件保存的路径
     * @param progress 进度回调
     */
	public void httpDownloadFile(final String url, final String filePath,final HttpClientDownLoadProgress progress) {
		downloadExcutorService.execute(new Runnable() {
			@Override
			public void run() {
				httpDownloadFile(url, filePath, progress, null);
			}
		});
	}

    /**
     * 下载文件
     * @param url 请求地址
     * @param filePath 文件保存的路径
     * @param progress 回调方法
     * @param headMap 请求参数
     */
	private void httpDownloadFile(String url, String filePath,HttpClientDownLoadProgress progress, Map<String, String> headMap) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(url);
			//设置请求的HEAD 
	        if(headMap!=null && !headMap.isEmpty()) {
	        	for (Map.Entry<String, String> item : headMap.entrySet()) {
	        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
	        			httpGet.addHeader(item.getKey(), item.getValue());
	        		}
				}
	        }
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				log.info(response.getStatusLine().toString());
				HttpEntity httpEntity = response.getEntity();
				long contentLength = httpEntity.getContentLength();
				InputStream is = httpEntity.getContent();
				// 根据InputStream 下载文件
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				byte[] buffer = new byte[1028*4];
				int r = 0;
				long totalRead = 0;
				while ((r = is.read(buffer)) > 0) {
					output.write(buffer, 0, r);
					totalRead += r;
					if (progress != null) {// 回调进度
						progress.onProgress((int) (totalRead * 100 / contentLength));
					}
				}
				File file=new File(filePath);
				if(!file.exists()) {
					file.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(filePath);
				output.writeTo(fos);
				output.flush();
				output.close();
				fos.close();
				EntityUtils.consume(httpEntity);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if(httpClient!=null) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}
    
	/**
	 * 上传文件
	 * 
	 * @param serverUrl 服务器地址
	 * @param localFilePath 本地文件路径
	 * @param serverFieldName服务器接收的参数名称
	 * @param params 请求参数
	 * @return
	 */
	public String httpUploadFile(String serverUrl, String localFilePath,String serverFieldName, Map<String, String> params){
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(serverUrl);
			FileBody binFileBody = new FileBody(new File(localFilePath));
 
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			
			multipartEntityBuilder.addPart(serverFieldName, binFileBody);
			
			//设置上传的其他参数
			if(params!=null &&!params.isEmpty()) {
				for (Map.Entry<String, String> item : params.entrySet()) {
	        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
	        			multipartEntityBuilder.addPart(item.getKey(),new StringBody(item.getValue(), ContentType.TEXT_PLAIN));
	        		}
				}
			}
 
			HttpEntity reqEntity = multipartEntityBuilder.build();
			httpPost.setEntity(reqEntity);
 
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		finally {
			try {
				if(httpClient!=null) {
					httpClient.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return result;
	}
	

	/**
	 * post 请求
	 * @param url 请求地址
	 * @param headMap head参数
	 * @param params post 参数
	 * @param KeyStoreType 密钥库类型 默认SSL
	 * @param keyStorePath 密钥库路径
	 * @param keyStorePassword 密钥库密码
	 * @return
	 * @throws Exception 
	 */
    public String httpSSLPost(String url,Map<String, String>headMap,String params,String KeyStoreType,String keyStorePath,String keyStorePassword) throws Exception { 
    	String result = "";
    	if(StringHelper.IsNullOrEmpty(KeyStoreType)) {
    		KeyStoreType=DEFAULT_KEYSTORE_TYPE;
    	}
        CloseableHttpClient httpclient =getSSLHttpClient(KeyStoreType,keyStorePath,keyStorePassword); 
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "charset=utf-8");
        
        //设置请求的HEAD 
        if(headMap!=null && !headMap.isEmpty()) {
        	for (Map.Entry<String, String> item : headMap.entrySet()) {
        		if(!StringHelper.IsNullOrEmpty(item.getKey())){
        			httpPost.addHeader(item.getKey(), item.getValue());
        		}
			}
        }
        
        // 设置请求的参数
        if(!StringHelper.IsNullOrEmpty(params)) {
        	StringEntity paramsEntity = new StringEntity(params,Consts.UTF_8);
            httpPost.setEntity(paramsEntity);
        }
        
        try {  
            CloseableHttpResponse response = httpclient.execute(httpPost);  
            try {  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    result = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                }  
            } finally {  
                response.close();  
            }  
        } catch (Exception e) {  
        	log.error(e.getMessage()); 
        } finally { 
            try {
            	if(httpclient!=null) {
            		httpclient.close();  
            	}
            } catch (IOException e) {  
            	log.error(e.getMessage());
            }  
        }  
        return result;
    }
	
    /**
	 * 获得秘钥库 KeyStore
	 * @param passWord 密码
	 * @param keyStorePath 秘钥库文件路径
	 * @return 秘钥库
	 * @throws Exception
	 * 
	 */
	private  KeyStore getStore(String type,String passWord,String keyStorePath) throws Exception{
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
	
	/**
	 * 获取httpClient
	 */
	private  CloseableHttpClient getSSLHttpClient(String KeyStoreType,String keyStorePath,String keyStorePassword) throws Exception{
		//获得秘钥库
		KeyStore keystore=getStore(KeyStoreType,keyStorePassword, keyStorePath);
		// 相信自己的CA和所有自签名的证书  
		//SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keystore, passWord.toCharArray()).build();
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keystore, new TrustSelfSignedStrategy()).build();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        //创建自定义的httpclient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        
        return httpClient;
	}
}