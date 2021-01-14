package com.bomber.manager.impl;

import java.util.List;

import com.bomber.manager.SummaryReportManager;
import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.model.SummaryReport;

@Repository
public class SummaryReportManagerImpl extends BaseManagerImpl<SummaryReport> implements SummaryReportManager {

	@Override
	@Transactional(readOnly = true)
	public List<SummaryReport> list(String recordId) {
		return find("from SummaryReport s where s.bombingRecord.id=?1", recordId);
	}

}
