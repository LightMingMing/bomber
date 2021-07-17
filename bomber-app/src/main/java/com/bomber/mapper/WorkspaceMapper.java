package com.bomber.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.Workspace;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface WorkspaceMapper extends PagingMapper<Integer, Workspace> {
}
