package com.dcits.brave.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
Usage
 @ChainMonitor(tags={
  @ChainTags(key="key1", value="value1"),
  @ChainTags(key="key2", value="value2")
})
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ChainMonitor {
	String value() default "";
	public ChainTags[] tags() default {};
}
