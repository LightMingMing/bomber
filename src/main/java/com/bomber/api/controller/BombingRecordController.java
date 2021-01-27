package com.bomber.api.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.service.BombingRecordService;

@RestController
@RequestMapping("/bombingRecords")
public class BombingRecordController {

	private final BombingRecordService bombingRecordService;

	public BombingRecordController(BombingRecordService bombingRecordService) {
		this.bombingRecordService = bombingRecordService;
	}

	@RequestMapping("/{id}/name")
	public String getRecordName(@PathVariable("id") String id) {
		return bombingRecordService.getRecordName(id);
	}
}
