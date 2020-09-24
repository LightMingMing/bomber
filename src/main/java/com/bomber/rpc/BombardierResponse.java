package com.bomber.rpc;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BombardierResponse {

	private int numConns;

	private int numReqs;

	private StatusStats status;

	private LatencyStats latency;

	private double tps;

	@Getter
	@Setter
	public static class StatusStats {

		private int req1xx;

		private int req2xx;

		private int req3xx;

		private int req4xx;

		private int req5xx;

		private int other;
	}

	@Getter
	@Setter
	public static class LatencyStats {

		private double max;

		private double min;

		private double avg;

		private double stdDev;

		private Percentiles percentiles;
	}

	@Getter
	@Setter
	public static class Percentiles {

		@JsonProperty("0.25")
		private double point25;

		@JsonProperty("0.5")
		private double point50;

		@JsonProperty("0.75")
		private double point75;

		@JsonProperty("0.9")
		private double point90;

		@JsonProperty("0.95")
		private double point95;

		@JsonProperty("0.99")
		private double point99;

	}
}
