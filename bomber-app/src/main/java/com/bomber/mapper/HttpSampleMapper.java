package com.bomber.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.HttpSample;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface HttpSampleMapper extends BaseMapper<Integer, HttpSample> {

	/**
	 * 根据组 ID 查询所有的 Http 请求脚本
	 *
	 * @param groupId 组 ID
	 * @return Http 请求脚本
	 */
	List<HttpSample> findAllByGroup(Integer groupId);

	/**
	 * Http 请求脚本排序
	 *
	 * @param ids 待排序的 Http 请求脚本 ID
	 * @return 修改的记录数
	 */
	int reorder(List<Integer> ids);

}
