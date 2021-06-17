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

import com.bomber.entity.FunctionConfigure;
import com.bomber.mapper.FunctionConfigureMapper;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/functionConfigures")
public class FunctionConfigureController implements CrudController<Integer, FunctionConfigure> {

	private final FunctionConfigureMapper functionConfigureMapper;

	public FunctionConfigureController(FunctionConfigureMapper functionConfigureMapper) {
		this.functionConfigureMapper = functionConfigureMapper;
	}

	@Override
	@PostMapping
	public int create(@RequestBody FunctionConfigure functionConfigure) {
		return functionConfigureMapper.create(functionConfigure);
	}

	@Override
	@DeleteMapping("/{id}")
	public int delete(@PathVariable Integer id) {
		return functionConfigureMapper.delete(id);
	}

	@Override
	@PutMapping
	public int update(@RequestBody FunctionConfigure functionConfigure) {
		return functionConfigureMapper.update(functionConfigure);
	}

	@Override
	@GetMapping("/{id}")
	public Optional<FunctionConfigure> select(@PathVariable Integer id) {
		return functionConfigureMapper.select(id);
	}

	@GetMapping
	public List<FunctionConfigure> findAllByWorkspace(@RequestParam Integer workspaceId) {
		return functionConfigureMapper.findAllByWorkspace(workspaceId);
	}

	@PutMapping("/reorder")
	public int reorder(@RequestBody List<Integer> ids) {
		return functionConfigureMapper.reorder(ids);
	}
}
