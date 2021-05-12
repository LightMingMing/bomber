package com.bomber.function.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.bomber.function.CitizenIdentification;
import com.bomber.function.Counter;
import com.bomber.function.FixedLengthString;
import com.bomber.function.Function;
import com.bomber.function.Properties;
import com.bomber.function.SQLQuery;
import com.bomber.function.util.NoSuchConstructorException;

/**
 * @author MingMing Zhao
 */
class FunctionConstructorsTest {

	protected static <T extends Function> T build(Class<T> type, Map<String, String> parameterValues) {
		return new FunctionConstructors<>(type).invokeMethod(parameterValues);
	}

	@Test
	public void testNoParameter() {
		build(Counter.class, Collections.emptyMap());
	}

	@Test
	public void testMapParameter() {
		Properties properties = build(Properties.class, Map.of("name", "MingMing"));
		assertThat(properties.execute()).containsEntry("name", "MingMing");
	}

	@Test
	public void testFixedLengthString() {
		FixedLengthString fixedLengthString = build(FixedLengthString.class,
				Map.of("length", "10", "prefix", "hello"));
		assertThat(fixedLengthString.execute()).isEqualTo("hello00000");
	}

	@Test
	public void testCitizenIdentification() {
		CitizenIdentification identification = build(CitizenIdentification.class, Map.of("foo", "bar"));
		assertThat(identification.execute()).hasSize(18);
	}

	@Test
	public void testNoSuchConstructor() {
		assertThatExceptionOfType(NoSuchConstructorException.class)
				.isThrownBy(() -> build(SQLQuery.class, Map.of("url", "url", "user", "user")));
	}
}