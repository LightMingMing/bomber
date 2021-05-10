package com.bomber.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 函数信息
 *
 * @author MingMing Zhao
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FuncInfo {

	/**
	 * 必须的参数, 多个参数以 ', ' 分割
	 *
	 * @return 必须的参数
	 */
	String requiredArgs() default "";

	/**
	 * 可选的参数, 多个参数以 ', ' 分割
	 *
	 * @return 必须的参数
	 */
	String optionalArgs() default "";

	/**
	 * 自定义的参数, 用于解决特殊函数的函数依赖问题
	 *
	 * @return 自定义参数
	 * @see MVEL
	 */
	String customArg() default "";

	/**
	 * 函数是否返回所有输入参数, 用于解决特殊函数的函数依赖问题
	 *
	 * @return 函数是否返回所有输入参数
	 * @see Properties
	 */
	boolean retAllArgs() default false;

	/**
	 * 函数返回的参数, 用于解决特殊函数的函数依赖问题
	 *
	 * @return 函数返回的参数
	 * @see SQLBatchQuery
	 * @see SQLQuery
	 * @see SQLMultiRowsQuery
	 */
	String retArg() default "";

	/**
	 * 函数是否支持并行执行, 提升函数执行效率
	 *
	 * @return 是否支持并行执行
	 * @see SQLBatchQuery
	 * @see SQLQuery
	 * @see SQLMultiRowsQuery
	 */
	boolean parallel() default false;
}
