package cn.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串帮助类
 *
 */
public class StringHelper{

	/**
	 * 判断是否是null或者“”
	 * @param string
	 * @return  boolean
	 */
	public static boolean  IsNullOrEmpty(String string){
		boolean flag=false;
		if(string==null||string.trim().isEmpty()){
			flag=true;
		}
		return flag;
	}

	/**
	 * 左侧补足
	 * @param src 目标字符串
	 * @param len 总长度
	 * @param ch  填充的字符
	 * @return
	 */
	public static String PadLeft(String src,int len,char ch){
		int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
	}
	/**
	 * 右侧补足
	 * @param src 目标字符串
	 * @param len 总长度
	 * @param ch  填充的字符
	 * @return
	 */
	public static String PadRight(String src,int len,char ch){
		int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
	}
	
	/**
	 * string  用 , 分割成list
	 * @param src  字符串
	 * @return
	 */
	public static List<String> ToList(String src) {
		return ToList(src,",");
	}
	
	/**
	 * string  用 , 分割成list
	 * @param src  字符串
	 * @param splitWord
	 * @return
	 */
	public static List<String> ToList(String src,String splitWord) {
		
		List<String> list=new ArrayList<String>();
		
		if(IsNullOrEmpty(src)){
			return list;
		}
		String[] arr=src.split(splitWord);
		if(arr!=null&&arr.length>0){
			list=Arrays.asList(arr);
		}
		return list;
	}
	
	/**
	 * string  用 , 分割成 int list
	 * @param src 字符串
	 * @return
	 */
	public static List<Integer> ToIntegerList(String src) {
		return ToIntegerList(src,",");
	}
	
	/**
	 * string  分割 int list
	 * @param src 字符串
	 * @param splitWord 分隔符
	 * @return List
	 */
	public static List<Integer> ToIntegerList(String src,String splitWord) {
		
		List<Integer> list=new ArrayList<Integer>();
		
		if(IsNullOrEmpty(src)){
			return list;
		}
		String[] arr=src.split(splitWord);
		if(arr!=null&&arr.length>0){
			for (String item: arr) {
					list.add(DataConvert.ToInteger(item));
			}
		}
		return list;
	}
	
	/**
	 * array 拼接 成 string 
	 * @param join 拼接字符
	 * @param array  数组
	 * @return string
	 */
	public  static  String  Join(String join, Object[] array){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<array.length;i++){
             if(i==(array.length-1)){
                 sb.append(array[i]);
             }else{
                 sb.append(array[i]).append(join);
             }
        }
        return new String(sb);
    }
	
	/**
	 * 按照字节来截取字符串
	 * @param str  源字符
	 * @param length 截取的字节数 
	 * @return
	 */
	public static String cutString(String str,int length) {
		return cutString(str,length,"...");
	}
	
	/**
	 * 按照字节来截取字符串
	 * @param str  源字符
	 * @param length 截取的字节数 
	 * @param dot 
	 * @return
	 */
	public static String cutString(String str,int length,String dot) {
		String result="";
		try {
			
			if(StringHelper.IsNullOrEmpty(str)) {
				return result;
			}
			byte[] bytes =str.getBytes("Unicode"); 
			
			if(length>bytes.length) {
				return str;
			}
			
	        int n = 0; // 表示当前的字节数  
	        int i = 2; // 要截取的字节数，从第3个字节开始  
	        for (; i < bytes.length && n < length; i++){  
	            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节  
	            if (i % 2 == 1){  
	                n++; // 在UCS2第二个字节时n加1  
	            }  
	            else{  
	                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节  
	                if (bytes[i] != 0){  
	                    n++;  
	                }  
	            }  
	        }  
	        // 如果i为奇数时，处理成偶数  
	        if (i % 2 == 1){  
	            // 该UCS2字符是汉字时，去掉这个截一半的汉字  
	            if (bytes[i - 1] != 0){  
	                i = i - 1;    
	            }  
	            // 该UCS2字符是字母或数字，则保留该字符  
	            else{  
	                i = i + 1;  
	            }  
	        }  
	  
	        result=new String(bytes, 0, i, "Unicode")+dot;
	        
		} catch (Exception e) {
			result=str;
		}
		
		return result;
	}
}
