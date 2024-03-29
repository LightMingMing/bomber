package com.bomber.action;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.struts.EntityAction;
import org.springframework.beans.factory.annotation.Autowired;

import com.bomber.model.BombingRecord;
import com.bomber.service.BomberService;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
public class BombingRecordAction extends EntityAction<BombingRecord> {

	private static final long serialVersionUID = -6295947087748811221L;

	@Autowired
	private BomberService bomberService;

	@Setter
	@Getter
	private String recordId;

	@Setter
	@Getter
	private String recordIds;

	public String displayChartByG2() {
		return "chartByG2";
	}

	public String compareByG2() {
		return "compareByG2";
	}

	public String pauseExecute() {
		bomberService.pauseExecute(Long.parseLong(this.getUid()));
		return SUCCESS;
	}

	public String continueExecute() {
		bomberService.continueExecute(Long.parseLong(this.getUid()));
		return SUCCESS;
	}
}
