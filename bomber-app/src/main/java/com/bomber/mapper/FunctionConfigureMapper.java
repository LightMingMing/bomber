package com.bomber.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.FunctionConfigure;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface FunctionConfigureMapper extends BaseMapper<Integer, FunctionConfigure> {

	/**
	 * 查询工作空间的所有函数配置
	 *
	 * @param workspaceId 工作空间 ID
	 * @return 函数配置
	 */
	List<FunctionConfigure> findAllByWorkspace(Integer workspaceId);

	/**
	 * 函数配置排序
	 *
	 * @param ids 待排序函数配置 ID
	 * @return 修改的记录数
	 */
	int reorder(List<Integer> ids);
}
