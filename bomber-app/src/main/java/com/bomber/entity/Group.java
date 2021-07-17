package com.bomber.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 分组
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class Group extends BaseEntity<Integer> {

	private static final long serialVersionUID = -3596428565099867920L;

	private Integer workspaceId;

	private String name;
}
