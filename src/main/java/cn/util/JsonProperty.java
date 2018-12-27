package cn.util;

import java.lang.annotation.ElementType;  
import java.lang.annotation.Retention;  
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.Target;  

@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
public @interface JsonProperty {

	/** 
     * 指定一个类型是JsonPropertyType的,表现为JsonPropertyType=默认为JsonPropertyType.Base的Annotation 
     * @return 
     */  
    JsonPropertyType JsonPropertyType() default JsonPropertyType.Base;  
    /** 
     * 指定一个类型是Class的,表现为cls=默认为String.class的Annotation 
     * @return 
     */  
    Class cls() default String.class;  
    
    /** 
     * 指定当前属性的类型 
     * 
     */  
    public enum JsonPropertyType{           
        JsonList,JsonObject,Base,Transient;  
    };  
	
}
