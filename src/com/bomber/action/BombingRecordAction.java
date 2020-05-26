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
	private String recordId;

	@Setter
	@Getter
	private String recordIds;

	public String displayChart() {
		return "chart";
	}

	public String compare() {
		return "compare";
	}
}
