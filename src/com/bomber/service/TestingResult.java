package com.bomber.service;

import com.bomber.model.LatencyStats;
import com.bomber.model.StatusStats;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestingResult {

	private int numConns;

	private int numReqs;

	private StatusStats status;

	private LatencyStats latency;

	private double tps;
}
