package com.bomber.manager;

import java.util.List;

import org.ironrhino.core.service.BaseManager;

import com.bomber.model.HttpSample;
import com.bomber.vo.SimpleHttpSample;

public interface HttpSampleManager extends BaseManager<HttpSample> {

	List<SimpleHttpSample> getSimpleHttpSampleList(String projectId);

	void updateOrderNumber(List<String> idList);
}
