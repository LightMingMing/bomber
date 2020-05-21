package com.bomber.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;

import com.bomber.model.SummaryReport;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class SummaryReportAction extends EntityAction<SummaryReport> {

	private static final long serialVersionUID = 6534356226561948777L;

	@Setter
	@Getter
	private String sampleId;

	public String displayChart() {
		return "chart";
	}
}
