package com.bomber.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.service.BombingRecordService;

@RestController
@RequestMapping("/bombingRecord")
public class BombingRecordController {

	private final BombingRecordService bombingRecordService;

	public BombingRecordController(BombingRecordService bombingRecordService) {
		this.bombingRecordService = bombingRecordService;
	}

	@RequestMapping("/getRecordName")
	public String getRecordName(@RequestParam("id") String id) {
		return bombingRecordService.getRecordName(id);
	}
}
