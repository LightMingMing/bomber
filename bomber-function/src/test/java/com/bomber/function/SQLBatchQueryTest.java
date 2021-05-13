package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;

/**
 * @author MingMing Zhao
 */
class SQLBatchQueryTest {

	private static final Integer[] batchSizes = new Integer[15];

	private static final Integer[] totalQueries = new Integer[15];

	static {
		for (int i = 0; i < batchSizes.length; i++) {
			batchSizes[i] = Math.min((1 << i), SQLBatchQuery.MAX_BATCH_SIZE);
			totalQueries[i] = i == 0 ? 0 : batchSizes[i - 1] + totalQueries[i - 1];
		}
	}

	private List<Map<String, String>> mockList(InvocationOnMock invocation) {
		int start = invocation.getArgument(0, Integer.class);
		int batchSize = invocation.getArgument(1, Integer.class);
		List<Map<String, String>> result = new ArrayList<>(batchSize);
		for (int i = start; i < start + batchSize; i++) {
			result.add(Map.of("id", Integer.toString(i)));
		}
		return result;
	}

	private SQLBatchQuery mockSQLBatchQuery() {
		return spy(new SQLBatchQuery("url", "user", "password", "sql", "ret") {
			@Override
			protected DataSource getDataSource() {
				// Don't create DataSource
				return null;
			}
		});
	}

	@Test
	public void testBatchQuery() {
		ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Integer> totalQuery = ArgumentCaptor.forClass(Integer.class);

		SQLBatchQuery query = mockSQLBatchQuery();
		willAnswer(this::mockList).given(query).executeBatchQuery(totalQuery.capture(), batchSize.capture());

		for (int i = 0; i < 10000; i++) {
			assertThat(query.execute()).containsEntry("id", i + "");
		}

		assertThat(batchSize.getAllValues()).startsWith(batchSizes);
		assertThat(totalQuery.getAllValues()).startsWith(totalQueries);
	}

	@Test
	public void testJump() {
		ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Integer> totalQuery = ArgumentCaptor.forClass(Integer.class);

		SQLBatchQuery query = mockSQLBatchQuery();
		willAnswer(this::mockList).given(query).executeBatchQuery(totalQuery.capture(), batchSize.capture());

		int offset = 1000;
		query.jump(offset);
		for (int i = 0; i < 10000; i++) {
			assertThat(query.execute()).containsEntry("id", (offset + i) + "");
		}

		assertThat(batchSize.getAllValues()).startsWith(batchSizes);

		Integer[] expectedTotalQueries = new Integer[totalQueries.length];
		for (int i = 0; i < totalQueries.length; i++) {
			expectedTotalQueries[i] = totalQueries[i] + offset;
		}
		assertThat(totalQuery.getAllValues()).startsWith(expectedTotalQueries);
	}

	private List<Map<String, String>> mockEmptyList(InvocationOnMock invocation) {
		return Collections.emptyList();
	}

	@Test
	public void testBatchQueryResultIsEmpty() {
		SQLBatchQuery query = mockSQLBatchQuery();

		willAnswer(this::mockEmptyList).given(query).executeBatchQuery(anyInt(), anyInt());

		for (int i = 0; i < 100; i++) {
			assertThat(query.execute()).isEmpty();
		}
	}
}
