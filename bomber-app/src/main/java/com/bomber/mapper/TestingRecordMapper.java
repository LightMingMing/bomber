package com.bomber.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.TestingRecord;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface TestingRecordMapper extends PagingMapper<Long, TestingRecord> {

	/**
	 * 查询 HttpSample 的所有测试记录
	 *
	 * @param httpSampleId httpSampleId
	 * @return 测试记录
	 */
	List<TestingRecord> findAllByHttpSample(Integer httpSampleId);


	int createOrUpdate(TestingRecord testingRecord);
}
