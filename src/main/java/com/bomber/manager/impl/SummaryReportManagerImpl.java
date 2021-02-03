package com.bomber.manager.impl;

import java.util.stream.Stream;

import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.SummaryReportManager;
import com.bomber.model.SummaryReport;
import com.bomber.vo.SummaryReportVo;

@Repository
public class SummaryReportManagerImpl extends BaseManagerImpl<SummaryReport> implements SummaryReportManager {

	@Override
	@Transactional(readOnly = true)
	public Stream<SummaryReportVo> list(String recordId) {
		return find("from SummaryReport s where s.bombingRecord.id=?1", recordId).stream().map(SummaryReportVo::map);
	}

}
