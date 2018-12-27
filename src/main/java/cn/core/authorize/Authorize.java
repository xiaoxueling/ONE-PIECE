package cn.core.authorize;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
	String setting();
	public enum AuthorizeType{ALL,ONE}
	AuthorizeType type() default AuthorizeType.ALL;
}
