package cn.util.wechat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HttpClictUtil extends HttpFather {
	public String SSLSendPost(String url, Map<String, Object> param,
			String type, String passWord, String keyStorePath) {
		return super.sendPost(HttpFather.pre_SSL, url, param, type, passWord,
				keyStorePath);
	}

	public String sendPost(String url, Map<String, Object> param) {
		return super.sendPost(null, url, param, null, null, null);
	}

	public  String sendGet(String url, String param) {
		return super.get(null, url, param, null, null, null);
	}

	public static String urlEncod(String url) {
		String soure = "";
		try {
			soure = URLEncoder.encode(url, "UTF-8");
		} catch (Exception ex) {

		}
		return soure;
	}
	
	public static String httpPostSend(String url, String param) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			// 开始http请求
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			conn.setConnectTimeout(new Integer(600).intValue());
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
