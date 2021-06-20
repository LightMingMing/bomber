package com.bomber.engine;

/**
 * 性能测试 Service
 *
 * @author MingMing Zhao
 */
public interface BombingService {

	/**
	 * 性能测试
	 *
	 * @param request 请求
	 * @return 记录 ID
	 */
	Long execute(BombingRequest request);

	/**
	 * 继续执行
	 *
	 * @param id 记录 ID
	 * @return 记录 ID
	 */
	Long continueExecute(Long id);

	/**
	 * 暂停执行
	 *
	 * @param id 记录 ID
	 */
	void pauseExecute(Long id);

}
