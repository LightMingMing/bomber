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
	 * 查询工作空间的 Http 请求脚本
	 *
	 * @param workspaceId 工作空间 ID
	 * @return Http 请求脚本
	 */
	List<HttpSample> findAllByWorkspace(Integer workspaceId);

	/**
	 * Http 请求脚本排序
	 *
	 * @param ids 待排序的 Http 请求脚本 ID
	 * @return 修改的记录数
	 */
	int reorder(List<Integer> ids);

}
