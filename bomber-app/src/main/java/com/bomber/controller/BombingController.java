package com.bomber.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.engine.BombingRequest;
import com.bomber.engine.BombingService;

/**
 * @author MingMing Zhao
 */
@RestController
@RequestMapping("/api/bomb")
public class BombingController {

	private final BombingService bombingService;

	public BombingController(BombingService bombingService) {
		this.bombingService = bombingService;
	}

	@PostMapping
	public Long execute(@RequestBody BombingRequest request) {
		return bombingService.execute(request);
	}

	@GetMapping("/continue")
	public Long continueExecute(@RequestParam Long id) {
		return bombingService.continueExecute(id);
	}

	@GetMapping("/pause")
	public void pauseExecute(@RequestParam Long id) {
		bombingService.pauseExecute(id);
	}

}
