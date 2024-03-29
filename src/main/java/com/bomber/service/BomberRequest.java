package com.bomber.service;

import java.util.List;

import com.bomber.engine.model.Scope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BomberRequest {

	private String httpSampleId;

	private String name;

	private int requestsPerThread;

	private List<Integer> threadGroups;

	private int beginUserIndex;

	private int iterations;

	private Scope scope;
}
