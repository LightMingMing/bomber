package com.bomber.manager;

import com.bomber.model.TestingRecord;
import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TestingRecordManagerImpl extends BaseManagerImpl<TestingRecord> implements TestingRecordManager {

	@Override
	@Transactional(readOnly = true)
	public List<TestingRecord> listByHttpSample(String httpSampleId) {
		return find("from TestingRecord r where r.httpSample.id=?1", httpSampleId);
	}

}
