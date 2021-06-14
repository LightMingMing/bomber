package com.bomber.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.entity.Workspace;
import com.bomber.mapper.WorkspaceMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController implements CrudController<Integer, Workspace> {

	private final WorkspaceMapper workspaceMapper;

	public WorkspaceController(WorkspaceMapper workspaceMapper) {
		this.workspaceMapper = workspaceMapper;
	}

	@Override
	@PostMapping
	public int create(@RequestBody Workspace workspace) {
		return workspaceMapper.create(workspace);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Integer id) {
		return workspaceMapper.delete(id);
	}

	@Override
	@PutMapping
	public int update(@RequestBody Workspace workspace) {
		return workspaceMapper.update(workspace);
	}

	@Override
	@GetMapping("/{id}")
	public Optional<Workspace> select(@PathVariable Integer id) {
		return workspaceMapper.select(id);
	}

	@GetMapping
	public Page<Workspace> paging(@RequestParam int page, @RequestParam int size) {
		return workspaceMapper.paging(PageRequest.of(page, size));
	}
}
