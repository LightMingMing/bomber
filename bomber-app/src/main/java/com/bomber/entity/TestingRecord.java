package com.bomber.entity;

import java.util.Date;
import java.util.List;

import com.bomber.engine.model.Scope;
import lombok.Getter;
import lombok.Setter;

/**
 * 测试记录
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class TestingRecord extends BaseEntity<Long> {

	private static final long serialVersionUID = -9066165070864783215L;

	/**
	 * Http 请求脚本 ID
	 */
	private Integer httpSampleId;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 线程组
	 */
	private List<Integer> threadGroups;

	/**
	 * 线程组游标 (动态变化)
	 */
	private int threadGroupCursor = 0;

	/**
	 * 活跃线程数 (动态变化)
	 */
	private int activeThreads = 0;

	/**
	 * 作用域
	 */
	private Scope scope;

	/**
	 * 起始用户位置
	 */
	private int beginUserIndex = 0;

	/**
	 * 迭代数
	 */
	private int iterations;

	/**
	 * 当前迭代数 (动态变化)
	 */
	private int currentIteration;

	/**
	 * 每线程请求数
	 */
	private int requestsPerThread;

	/**
	 * 运行状态
	 */
	private Status status;

	/**
	 * 记录创建时间
	 */
	private Date createTime;

	/**
	 * 测试开始时间
	 */
	private Date startTime;

	/**
	 * 测试结束时间
	 */
	private Date endTime;

	/**
	 * 备注
	 */
	private String remark;
}
