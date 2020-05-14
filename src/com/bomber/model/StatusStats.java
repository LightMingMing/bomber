package com.bomber.model;

import lombok.Getter;
import lombok.Setter;
import org.ironrhino.core.util.JsonUtils;

@Getter
@Setter
public class StatusStats {
	private int req1xx;
	private int req2xx;
	private int req3xx;
	private int req4xx;
	private int req5xx;
	private int other;

	@Override
	public String toString() {
		try {
			return JsonUtils.toJson(this);
		} catch (Exception e) {
			return super.toString();
		}
	}
}
