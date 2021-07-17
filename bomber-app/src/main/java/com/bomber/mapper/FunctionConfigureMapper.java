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
	 * 根据组 ID 查询的所有函数配置
	 *
	 * @param groupId 组 ID
	 * @return 函数配置
	 */
	List<FunctionConfigure> findAllByGroup(Integer groupId);

	/**
	 * 函数配置排序
	 *
	 * @param ids 待排序函数配置 ID
	 * @return 修改的记录数
	 */
	int reorder(List<Integer> ids);
}
