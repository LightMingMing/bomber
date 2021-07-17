package com.bomber.engine;

import java.util.List;

import org.springframework.lang.NonNull;

import com.bomber.engine.model.Scope;
import lombok.Getter;
import lombok.Setter;

/**
 * 性能测试请求
 *
 * @author MingMing Zhao
 */
@Getter
@Setter
public class BombingRequest {

	@NonNull
	private Integer httpSampleId;

	@NonNull
	private String name;

	private int requestsPerThread;

	@NonNull
	private List<Integer> threadGroups;

	private int beginUserIndex;

	private int iterations = 1;

	@NonNull
	private Scope scope;
}
