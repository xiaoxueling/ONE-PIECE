package cn.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.util.JsonProperty.JsonPropertyType;

public class JsonUtil {

	/**
	 * bean中的属性只能够是如下几种包装类型; 已经用Annotation注释的list和object;
	 */
	private static final String type_boolean = "boolean",
			type_date = "date",
			type_float = "float",
			type_double = "double",
			type_long = "long",
			type_integer = "integer",
			type_string = "string",
			type_int="int";
	/**
	 * 查看一个字符串是否是一[开头,即是否有可能是数组;
	 */
	private static final Pattern pattern = Pattern.compile("^([\\s]{0,}\\[).*");

	/**
	 * 
	 * @param jsonString json字符串
	 * @param cls 需要转换的class类
	 * @return
	 */
	public static <T  extends Object> List<T> parseToArray(String jsonString,Class<T> cls) {
		try {
			JSONArray jsonArr = JSON.parseArray(jsonString);
			List<Object> list = new ArrayList<Object>();
			Object o;
			for (int i = 0; i < jsonArr.size(); i++) {
				String jarString = jsonArr.getString(i);
				Matcher matcher = pattern.matcher(jarString);
				if (matcher.find()) {// 如果数组中包含的仍然是一个数组
					try {
						o = parseToArray(jarString, cls);
					} catch (JSONException e) {
						o = parseToObject(jarString, cls);
					}
				} else {
					o = parseToObject(jarString, cls);
				}
				list.add(o);
			}
			return (List<T>) list;
		}catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param jsonString json字符串
	 * @param cls 需要转换的class类
	 * @return
	 */
	public static <T extends Object> T parseToObject(String jsonString, Class<T> cls) {
		try {
			T o = cls.newInstance();
			JSONObject jsonObj = (JSONObject) JSONObject.parse(jsonString);
			Field[] fields = cls.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				Field field = fields[j];
				field.setAccessible(true);
				Annotation[] annotations = field.getAnnotations();
				// 为自定义的Annotation赋值
				JsonProperty jsonProperty = null;
				for (int a = 0; a < annotations.length; a++) {
					if (annotations[a] instanceof JsonProperty) {
						jsonProperty = (JsonProperty) annotations[a];
						break;
					}
				}
				String fileName=field.getName();
				Object tmp = jsonObj.get(fileName);
				if (jsonProperty == null || jsonProperty.JsonPropertyType() == null|| jsonProperty.JsonPropertyType() == JsonPropertyType.Base) {
					
					String typeString = field.getGenericType().toString().toLowerCase();
					if (tmp != null) {
						if (typeString.contains(type_boolean)) {
							field.set(o, jsonObj.getBoolean(fileName));
						} else if (typeString.contains(type_date)) {
							field.set(o, jsonObj.getDate(fileName));
						} else if (typeString.contains(type_integer)) {
							field.set(o, jsonObj.getInteger(fileName));
						} else if (typeString.contains(type_long)) {
							field.set(o, jsonObj.getLong(fileName));
						} else if (typeString.contains(type_float)) {
							field.set(o, jsonObj.getFloat(fileName));
						} else if (typeString.contains(type_double)) {
							field.set(o, jsonObj.getDouble(fileName));
						} else if (typeString.contains(type_string)) {
							field.set(o, jsonObj.getString(fileName));
						}else if (typeString.contains(type_int)) {
							field.set(o, jsonObj.getIntValue(fileName));
						} else {
							field.set(o, tmp);
						}
					}
				} else if (jsonProperty.JsonPropertyType() == JsonPropertyType.JsonObject) {
					if (tmp != null) {
						field.set(o,parseToObject(tmp.toString(), jsonProperty.cls()));
					}	
				} else if (jsonProperty.JsonPropertyType() == JsonPropertyType.JsonList) {
					if (tmp != null) {
						field.set(o,parseToArray(tmp.toString(), jsonProperty.cls()));
					}	
				}
			}
			return o;
		} catch (Exception e) {
			return null;
		}
	}
}
