package com.bomber.functions.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncInfo {

	String requiredArgs() default "";

	String optionalArgs() default "";

	String customArg() default "";

	boolean retAllArgs() default false;

	String retArg() default "";

	boolean parallel() default false;
}
