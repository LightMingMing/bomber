package com.bomber.function;

/**
 * 生产者函数
 *
 * @author MingMing Zhao
 * @see CitizenIdentification 身份证
 * @see Counter 计数器
 * @see FixedLengthRandom 定长随机数
 * @see FixedLengthString 定长字符串
 * @see Properties 属性
 * @see Random 随机数
 * @see SQLBatchQuery SQL 批量查询
 * @see UUID UUID
 */
public interface Producer<T> extends Function {
	T execute();
}
