package com.bomber.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;

import com.bomber.functions.core.Input;

public class SQLBatchQueryTest {

	private List<Map<String, String>> mockList(InvocationOnMock invocation) {
		int start = invocation.getArgument(0, Integer.class);
		int batchSize = invocation.getArgument(1, Integer.class);
		List<Map<String, String>> result = new ArrayList<>(batchSize);
		for (int i = start; i < start + batchSize; i++) {
			result.add(Collections.singletonMap("id", Integer.toString(i)));
		}
		return result;
	}

	@Test
	public void testBatchQuery() {
		ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
		ArgumentCaptor<Integer> count = ArgumentCaptor.forClass(Integer.class);

		SQLBatchQuery query = spy(new SQLBatchQuery());
		willAnswer(this::mockList).given(query).executeBatchQuery(count.capture(), batchSize.capture());

		Input input = new Input("count", "0");
		for (int i = 0; i < 10000; i++) {
			assertThat(query.execute(input)).containsEntry("id", i + "");
		}

		assertThat(batchSize.getAllValues()).startsWith(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 1024);
		assertThat(count.getAllValues()).startsWith(0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 3071);
	}

	private List<Map<String, String>> mockEmptyList(InvocationOnMock invocation) {
		return Collections.emptyList();
	}

	@Test
	public void testBatchQueryResultIsEmpty() {
		SQLBatchQuery query = spy(new SQLBatchQuery());

		willAnswer(this::mockEmptyList).given(query).executeBatchQuery(anyInt(), anyInt());

		Input input = new Input("count", "0");
		for (int i = 0; i < 100; i++) {
			assertThat(query.execute(input)).isEmpty();
		}
	}

}