package com.bomber.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bomber.manager.BombingRecordManager;
import com.bomber.model.BombingRecord;
import com.bomber.service.BombingRecordService;

@Service
public class BombingRecordServiceImpl implements BombingRecordService {

	private final BombingRecordManager bombingRecordManager;

	public BombingRecordServiceImpl(BombingRecordManager bombingRecordManager) {
		this.bombingRecordManager = bombingRecordManager;
	}

	@Override
	@Transactional(readOnly = true)
	public String getRecordName(Long id) {
		BombingRecord record = bombingRecordManager.get(id);
		if (record == null)
			return null;
		String recordName = record.getName();
		String requestName = record.getHttpSample().getName();
		return recordName.startsWith(requestName) ? recordName : requestName + "-" + recordName;
	}
}
