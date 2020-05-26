package com.bomber.engine;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BomberPlan {

	private String sampleId;

	private String name;

	private List<Integer> threadGroup;

	private int requestsPerThread;
}
