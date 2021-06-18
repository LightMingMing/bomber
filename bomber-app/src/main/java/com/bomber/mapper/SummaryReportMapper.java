package com.bomber.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.bomber.entity.SummaryReport;

/**
 * @author MingMing Zhao
 */
@Mapper
public interface SummaryReportMapper extends PagingMapper<Long, SummaryReport> {

	/**
	 * 查询 TestingRecord 下的所有测试记录
	 *
	 * @param testingRecordId Testing Record ID
	 * @return 测试记录
	 */
	List<SummaryReport> findAllByTestingRecord(Long testingRecordId);


	@Override
	default int update(SummaryReport summaryReport) {
		throw new UnsupportedOperationException("update summary report is not supported");
	}
}
