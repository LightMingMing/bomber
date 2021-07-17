package com.bomber.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.Group;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface GroupMapper extends BaseMapper<Integer, Group> {

	/**
	 * 查询工作空间内的所有分组
	 *
	 * @param workspaceId 工作空间 ID
	 * @return 所有分组
	 */
	List<Group> findAllByWorkspace(Integer workspaceId);

}
