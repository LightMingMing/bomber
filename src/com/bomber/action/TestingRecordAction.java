package com.bomber.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;

import com.bomber.model.TestingRecord;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class TestingRecordAction extends EntityAction<TestingRecord> {

	@Setter
	@Getter
	private String sampleId;

	public String displayChart() {
		return "chart";
	}
}
