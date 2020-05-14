package com.bomber.manager;

import com.bomber.model.TestingRecord;
import org.ironrhino.core.service.BaseManager;

import java.util.List;

public interface TestingRecordManager extends BaseManager<TestingRecord> {

	List<TestingRecord> listByHttpSample(String httpSampleId);

}
