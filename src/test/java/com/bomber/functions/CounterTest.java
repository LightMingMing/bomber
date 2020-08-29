package com.bomber.functions;

import static org.assertj.core.api.Assertions.*;

import com.bomber.functions.core.Input;
import org.junit.Test;

public class CounterTest {

	@Test
	public void testExecute() {
		Input input = Input.EMPTY;

		Counter counter = new Counter();

		assertThat(counter.execute(input)).isEqualTo("0");

		counter.jump(100);

		assertThat(counter.execute(input)).isEqualTo("101");
	}

}