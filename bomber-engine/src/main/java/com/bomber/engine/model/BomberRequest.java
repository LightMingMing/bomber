package com.bomber.engine.model;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

/**
 * 请求
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class BomberRequest {

	/**
	 * 任务 ID
	 */
	@NonNull
	private String id;

	/**
	 * 名称
	 */
	@NonNull
	private String name;

	/**
	 * 脚本
	 */
	@NonNull
	private HttpRequest httpRequest;

	/**
	 * 每线程请求数
	 */
	private int requestsPerThread;

	/**
	 * 线程数组
	 */
	@NonNull
	private List<Integer> threadGroups;

	/**
	 * 迭代次数
	 */
	private int iterations = 1;

	/**
	 * 当前线程组, 用于暂停后继续执行
	 */
	private int threadGroupCursor = 0;

	/**
	 * 当前迭代数, 用于暂停后继续执行
	 */
	private int iteration = 0;

	/**
	 * 有效载荷
	 */
	@Nullable
	private Payload payload;

}
