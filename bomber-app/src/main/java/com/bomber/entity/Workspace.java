package com.bomber.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 工作空间
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class Workspace extends BaseEntity<Integer> {

	private static final long serialVersionUID = 4123905980506129142L;

	private String name;

	private String author;

	private String description;

	private Date createDate;

	private Date modifyDate;

}
