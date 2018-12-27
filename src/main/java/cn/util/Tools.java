package cn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

public class Tools {
	

	/**
	 * MD5加密 32位小写
	 * 
	 * @param text
	 * @return
	 */
	public static String Md5(String text) {
		String result = "";
		try {
			/*MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes("UTF-8"));
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			result = buf.toString().toLowerCase();*/
			
			result=DigestUtils.md5DigestAsHex(text.getBytes("UTF-8")).toLowerCase();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 上传单张图片
	 * 
	 * @author dhr
	 * @param files
	 *            文件
	 * @param path
	 *            上传地址
	 * @return
	 */
	public static String saveUploadFile(MultipartFile files, String path) {
		// 上传图片
		String fileSavePath = "";
		// 上传的图片保存路径
		String filePath = path;
		File pathFile = new File(filePath);// 建文件夹
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		MultipartFile upFile = files;
		if (files.isEmpty()) {
			fileSavePath = "";
		} else {
			String newFileName = UUID.randomUUID().toString() + Tools.getFileExtension(upFile.getOriginalFilename());
			String newFilePath = filePath + newFileName;// 新路径
			File newFile = new File(newFilePath);
			try {
				upFile.transferTo(newFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileSavePath = Tools.getFileVirtualPath(newFilePath);// 保存到数据库的路径
		}
		return fileSavePath;
	}

	public static String getUploadDir() {
		SimpleDateFormat sdfFolderName = new SimpleDateFormat("yyyyMMdd");
		String newFolderName = sdfFolderName.format(new Date());
		String userDir = System.getProperty("user.dir");
		String path = userDir + "\\upload\\" + newFolderName + "\\";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static String getFileVirtualPath(String path) {
		return path.replace(System.getProperty("user.dir"), "").replaceAll("\\\\", "/");
	}

	public static String getFileExtension(String file) {
		if (StringHelper.IsNullOrEmpty(file) || file.lastIndexOf(".") == -1) {
			return "";
		}
		return file.substring(file.lastIndexOf("."));
	}

	/**
	 * 生成随机数
	 * @param length 长度
	 * @return
	 */
	public static String getRandomNum(int length) {
		String str = "0123456789qwertyuioplkjhgfdsazxcvbnm";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			int number = random.nextInt(36);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static String getOnlynumber(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		sb.append("123456789".charAt(random.nextInt(9)));
		for (int i = 0; i < length - 1; ++i) {
			int number = random.nextInt(10);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static String getTextFromWord(String path) {
		File file = new File(path);
		String text = "";
		try {
			InputStream is = new FileInputStream(file);
			WordExtractor ex = new WordExtractor(is);
			text = ex.getText();
		} catch (Exception e) {
			OPCPackage opcPackage;
			try {
				opcPackage = POIXMLDocument.openPackage(path);
				POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
				text = extractor.getText();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return text;
	}

	/**
	 * 返回随机数
	 * 
	 * @param list
	 * @author dhr
	 * @param selected
	 *            备选数量
	 * @return
	 */
	public static List getRandomNum(List list, int selected) {
		List reList = new ArrayList();
		Random random = new Random();
		// 先抽取，备选数量的个数
		if (list.size() >= selected) {
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		} else {
			// 如果数量超出
			selected = list.size();
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		}
		return reList;
	}

	/**
	 * * 设置cookie * 
	 * @param response 
	 * @param name cookie名字 
	 * @param value cookie值
	 * @param maxAge cookie生命周期 以秒为单位
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		// 生存周期默认时间为秒，如果设置为负值的话，则为浏览器进程Cookie(内存中保存)，关闭浏览器就失效。如果设置成0，删除Cookie
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

	/**
	 * 获取cookie里面的微信信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getCookieByName(HttpServletRequest request) {
		Cookie[] cookies = ((HttpServletRequest) request).getCookies();
		Map<String, String> map = new HashMap<String, String>();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (c.getName().equals("answer_openid")) {
					map.put("answer_openid", c.getValue());
					continue;
				}
				if (c.getName().equals("answer_access_token")) {
					map.put("answer_access_token", c.getValue());
					continue;
				}
				if (c.getName().equals("answer_refresh_token")) {
					map.put("answer_refresh_token", c.getValue());
					continue;
				}
			}
		}
		return map;
	}

	/**
	 * 将list1中包含的list2的值去掉
	 * 
	 * @param list1
	 *            要改变的list
	 * @param list2
	 *            要去除的list
	 * @return
	 */
	public static List<String> listRemoveList(List<String> list1, List<String> list2) {
		List<String> newList = new ArrayList<String>();
		Collection exists = new ArrayList<String>(list1);
		exists.removeAll(list2);
		newList = (List<String>) exists;
		return newList;
	}

	/**
	 * 随机打乱list中元素的顺序
	 * 
	 * @param list
	 * @return
	 */
	public static List shuffle(List<Map> list) {
		int size = list.size();
		Random random = new Random();

		for (int i = 0; i < size; i++) {
			// 获取随机位置
			int randomPos = random.nextInt(size);

			// 当前元素与随机元素交换
			Map temp = list.get(i);
			list.set(i, list.get(randomPos));
			list.set(randomPos, temp);
		}
		return list;
	}

	/**
	 * 过滤html标签
	 * 
	 * @param String
	 *            字符串
	 * @return 文本
	 */
	public static String delHTMLTag(String htmlStr,String type) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
		String regEx_img = "<img[^>]*>";
		if("script".equals(type)) {
			Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
		}else if("style".equals(type)) {
			Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
		}else if("html".equals(type)) {
			Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
		}else if("img".equals(type)) {
			Pattern p_img = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
			Matcher m_img = p_img.matcher(htmlStr);
			htmlStr = m_img.replaceAll(""); // 过滤html标签
		}
		return htmlStr.trim(); // 返回文本字符串
	}

	/**
	 * 获取登录IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}

	/**
	 * 文件上传路径
	 * 
	 * @param moduleName
	 *            功能模块名称
	 * @return
	 */
	public static String getUploadDir(String moduleName) {
		SimpleDateFormat sdfFolderName = new SimpleDateFormat("yyyyMM");
		String newFolderName = sdfFolderName.format(new Date());
		String userDir = System.getProperty("user.dir");
		String path = userDir + "\\upload\\" + moduleName + "\\" + newFolderName + "\\";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	/**
	 * 检查字符串数组中是否有空字符串
	 * 
	 * @param params
	 * @return boolean
	 */
	public static boolean checkParams(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (!isNotEmpty(params[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查字符串中是否有空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(Object str) {
		if (str == null || "".equals(str) || "null".equals(str)) {
			return false;
		}
		return true;
	}
}
