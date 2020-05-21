package com.bomber.manager;

import java.util.List;

import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.model.SummaryReport;

@Repository
public class SummaryReportManagerImpl extends BaseManagerImpl<SummaryReport> implements SummaryReportManager {

	@Override
	@Transactional(readOnly = true)
	public List<SummaryReport> listByHttpSample(String httpSampleId) {
		return find("from SummaryReport s where s.httpSample.id=?1", httpSampleId);
	}

}
