package com.bomber.model;

import lombok.Getter;
import lombok.Setter;
import org.ironrhino.core.util.JsonUtils;

import java.io.Serializable;

@Getter
@Setter
public class LatencyStats implements Serializable {

	private static final long serialVersionUID = -5283805144059478414L;

	private double max;
	private double avg;
	private double stdDev;
	private Percentiles percentiles;

	@Override
	public String toString() {
		try {
			return JsonUtils.toJson(this);
		} catch (Exception e) {
			return super.toString();
		}
	}
}
