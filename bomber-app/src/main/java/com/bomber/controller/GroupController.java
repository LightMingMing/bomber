package com.bomber.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.entity.Group;
import com.bomber.mapper.GroupMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/groups")
public class GroupController implements CrudController<Integer, Group> {

	private final GroupMapper groupMapper;

	public GroupController(GroupMapper groupMapper) {
		this.groupMapper = groupMapper;
	}

	@Override
	@PostMapping
	public int create(@RequestBody Group group) {
		return groupMapper.create(group);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Integer id) {
		return groupMapper.delete(id);
	}

	@Override
	@PutMapping
	public int update(@RequestBody Group group) {
		return groupMapper.update(group);
	}

	@Override
	@GetMapping("/{id}")
	public Optional<Group> select(@PathVariable Integer id) {
		return groupMapper.select(id);
	}

	@GetMapping
	public List<Group> findAllByWorkspace(@RequestParam Integer workspaceId) {
		return groupMapper.findAllByWorkspace(workspaceId);
	}
}
