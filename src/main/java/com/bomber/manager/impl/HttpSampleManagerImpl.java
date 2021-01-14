package com.bomber.manager.impl;

import com.bomber.manager.HttpSampleManager;
import org.ironrhino.core.service.BaseManagerImpl;
import org.springframework.stereotype.Repository;

import com.bomber.model.HttpSample;

@Repository
public class HttpSampleManagerImpl extends BaseManagerImpl<HttpSample> implements HttpSampleManager {
}
