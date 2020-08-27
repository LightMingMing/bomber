package com.bomber.functions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.ironrhino.core.validation.validators.CitizenIdentificationNumberValidator;
import org.junit.Test;

public class CitizenIdentificationFunctionTest extends BaseFunctionExecutor<CitizenIdentificationFunction> {

	private final Predicate<String> isValid = CitizenIdentificationNumberValidator::isValid;

	@Test
	public void test() {
		Map<String, String> params = new HashMap<>();
		params.put("addressCode", "410223");
		params.put("startDate", "20200101");

		Function<String> func = newFunction(params);

		assertThat(func.execute()).startsWith("41022320200101000").matches(isValid);
		assertThat(func.execute()).startsWith("41022320200101001").matches(isValid);

		execute(func, 1000);
		assertThat(func.execute()).startsWith("41022320200102002").matches(isValid);

		execute(func, 1000 * 31);
		assertThat(func.execute()).startsWith("41022320200202003").matches(isValid);

		execute(func, 1000 * 29); // 2020 is Leap year
		assertThat(func.execute()).startsWith("41022320200302004").matches(isValid);

		func = newFunction(params);
		execute(func, 1000 * 366);
		assertThat(func.execute()).startsWith("41022320210101000").matches(isValid);
	}

	@Test
	public void testSkip() {
		Map<String, String> params = new HashMap<>();
		params.put("addressCode", "410223");
		params.put("startDate", "20200101");

		Function<String> func1 = newFunction(params);
		Function<String> func2 = newFunction(params);

		func1.skip(10);
		execute(func2, 10);
		assertThat(func1.execute()).isEqualTo(func2.execute());

		func1.skip(100);
		execute(func2, 100);

		assertThat(func1.execute()).isEqualTo(func2.execute());

		func1.skip(1000);
		execute(func2, 1000);
		assertThat(func1.execute()).isEqualTo(func2.execute());
	}
}