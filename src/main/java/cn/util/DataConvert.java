package cn.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 数据转化
 */
public class DataConvert {

	private  static final Logger  log=LoggerFactory.getLogger(DataConvert.class);
	
	private  static final char[] cHex={ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * ToString
	 * 
	 * @param obj
	 * @return null 返回空
	 */
	public static String ToString(Object obj) {
		return ToString(obj, "");
	}

	/**
	 * ToString
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static String ToString(Object obj, String defaultValue) {
		try {
			return obj == null ? defaultValue : obj.toString();
		}catch(Exception e){
			return defaultValue;
		}
	}

	/**
	 * ToInteger
	 * 
	 * @param obj
	 * @return Integer
	 */
	public static Integer ToInteger(Object obj) {
		return ToInteger(obj, 0);
	}

	/**
	 * To Integer
	 * 
	 * @param Object
	 * @param defaultValue
	 * @return Integer
	 */
	public static Integer ToInteger(Object obj, Integer defaultValue) {
		try {
			return obj == null ? defaultValue : Integer.parseInt(ToString(obj,
					"0"));
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * ToLong 默认 0l
	 * 
	 * @param obj
	 * @return
	 */
	public static Long ToLong(Object obj) {
		return ToLong(obj, 0l);
	}

	/**
	 * ToLong
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static Long ToLong(Object obj, Long defaultValue) {
		try {
			return obj == null ? defaultValue : Long.parseLong(ToString(obj,
					"0"));
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * ToFloat 默认 0f
	 * 
	 * @param obj
	 * @return
	 */
	public static Float ToFloat(Object obj) {
		return ToFloat(obj, 0f);
	}

	/**
	 * ToFloat
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static Float ToFloat(Object obj, Float defaultValue) {
		try {
			return obj == null ? defaultValue : Float.parseFloat(ToString(obj,
					"0"));
		} catch (Exception e) {
		}
		return defaultValue;

	}

	/**
	 * ToDouble 默认 0d
	 * 
	 * @param obj
	 * @return
	 */
	public static Double ToDouble(Object obj) {
		return ToDouble(obj, 0d);
	}

	/**
	 * ToDouble
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static Double ToDouble(Object obj, Double defaultValue) {
		try {
			return obj == null ? defaultValue : Double.parseDouble(ToString(
					obj, "0"));
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * ToBoolean 默认false
	 * 
	 * @param obj
	 * @return boolean
	 */
	public static boolean ToBoolean(Object obj) {
		return ToBoolean(obj, false);
	}

	/**
	 * ToBoolean
	 * 
	 * @param Object
	 * @param defaultValue
	 * @return boolean
	 */
	public static boolean ToBoolean(Object obj, boolean defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		List<String> trueList = new ArrayList<String>();
		trueList.add("true");
		trueList.add("1");
		trueList.add("yes");
		return trueList.contains(ToString(obj).toLowerCase());
	}
	
	/**
	 * ToDate 默认 null
	 * @param obj
	 * @return
	 */
	public static Date ToDate(Object obj){
		return CalendarUntil.ParseDate(obj);
	}
	
	/**
	 * ToDate
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static Date ToDate(Object obj,Date defaultValue){
		return CalendarUntil.ParseDate(obj, defaultValue);
	}
	
	/**
	 * 对象转json字符串
	 * @param entity
	 * @return
	 */
	public static<T extends Object>String objectToJson(T obj){
		String jsonStr="";
		try {
			try {
				if(obj==null) {
					return jsonStr;
				}
				ObjectMapper objectMapper = new ObjectMapper();
				jsonStr=objectMapper.writeValueAsString(obj);
			} catch (Exception e) {
				jsonStr=JSON.toJSONString(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonStr;
	}

	/**
	 * json字符串转对象
	 * @param c
	 * @param jsonStr
	 * @return T
	 */
	public  static <T extends Object> T JsonToObject(String jsonStr,Class<T>c) {
		T object=null;
		try {
			try {
				object=c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return object;
			}
			if(StringHelper.IsNullOrEmpty(jsonStr)) {
				return object;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				object = objectMapper.readValue(jsonStr, c);
			} catch (IOException e) {
				object=JSON.parseObject(jsonStr, c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * json转Map
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> JsonToMap(String json) {
		Map<String, Object> map = null;
		try {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				map = objectMapper.readValue(json, HashMap.class);
			} catch (IOException e) {
				map =JSON.parseObject(json, HashMap.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * json转List
	 * @param json
	 * @return
	 */
	public static List<?> JsonTolist(String json) {
		List<?> list =null;
		try {
			list = new ObjectMapper().readValue(json, ArrayList.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * json字符串转list
	 * @param c
	 * @param jsonStr
	 * @return T
	 */
	public  static <T  extends Object> List<T> JsonToList(String jsonStr,Class<T>c) {
		return JsonUtil.parseToArray(jsonStr, c);
	}
	
	/**
	 * xml 转实体类
	 * @param c 实体类型
	 * @param strXml xmlStr
	 * @return T
	 */
	public static <T> T xmlToEntity(Class<T>c,String strXml){
		T entity=null;
		try {
			try {
				entity=c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return entity;
			}
			
			if (strXml.length() <= 0 || strXml == null)
				return entity;
			// 将字符串转化为XML文档对象
			Document document = DocumentHelper.parseText(strXml);
			// 获得文档的根节点
			Element root = document.getRootElement();
			// 遍历根节点下所有子节点
			Iterator<?> iter = root.elementIterator();

			//利用反射机制，调用set方法
			while(iter.hasNext()){
				Element ele = (Element)iter.next();
				//获取set方法中的参数字段（实体类的属性）
				Field field = c.getDeclaredField(ele.getName());
				//获取set方法，field.getType())获取它的参数数据类型
				Method method = c.getDeclaredMethod("set"+ele.getName(), field.getType());
				//调用set方法
				method.invoke(entity, ele.getText());
			}
		} catch (Exception e) {
			log.error("xml 格式异常: "+ strXml);
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 
	 * 将map排序后转成字符串	 
	 * @param map
	 * @return a=1&b=2
	 */
	public static String mapToString(Map<String,Object> map){
		StringBuilder param=new StringBuilder();
		map=new TreeMap<String, Object>();
		Set<String> keyMap=map.keySet();
		for(String key:keyMap){
			if(key.equals("key")){
				continue;
			}
			Object obj=map.get(key);
			if(obj!=null){
				String value=obj.toString().trim();
				if(value!=""){
					param.append(key).append("=").append(obj.toString().trim()).append("&");
				}
			}
		}
		String par=param.toString();
		String strPar=par.substring(0,par.length()-1);
		return strPar;
	}
	
	/**
	 * 将字节数组转换为十六进制字符串	 
	 * @param byte
	 * @return
	 */
	public static String byteToStr(byte[] by){
		
		String strDesign="";
		for(int i=0;i<by.length;i++){
			strDesign+=byteToHexStr(by[i]);
		}
		return strDesign;
	}
	/**
	 * 将字节转换为十六进制字符串 
	 * @param mByte
	 * @return
	 */
	public static String byteToHexStr(byte mByte){
		char[] mChr=new char[2];
		mChr[0]=cHex[(mByte >>> 4) & 0X0f];
		mChr[1]=cHex[ mByte & 0X0f];
		String s=new String(mChr);
		return s;
	} 
	
	/**
	 *  将字节数组转换为十六进制字符串	
	 * @param hash
	 * @return
	 */
	public static String byteToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
	
}
