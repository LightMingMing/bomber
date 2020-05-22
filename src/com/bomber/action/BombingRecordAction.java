package com.bomber.action;

import lombok.Getter;
import lombok.Setter;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;

import com.bomber.model.BombingRecord;

@AutoConfig
public class BombingRecordAction extends EntityAction<BombingRecord> {
	private static final long serialVersionUID = -6295947087748811221L;

	@Setter
	@Getter
	private String bombingId;

	@Setter
	@Getter
	private String bombingIds;

	public String displayChart() {
		return "chart";
	}

	public String compare() {
		return "compare";
	}
}
