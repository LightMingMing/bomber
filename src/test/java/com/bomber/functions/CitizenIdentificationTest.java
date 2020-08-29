package com.bomber.functions;

import static com.bomber.functions.core.Input.EMPTY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.function.Predicate;

import org.ironrhino.core.validation.validators.CitizenIdentificationNumberValidator;
import org.junit.Test;

import com.bomber.functions.core.Input;

public class CitizenIdentificationTest {

	private final Predicate<String> isValid = CitizenIdentificationNumberValidator::isValid;

	@Test
	public void testExecute() {
		CitizenIdentification func = new CitizenIdentification();
		func.init(new Input("addressCode", "410223", "startDate", "20200101"));

		assertThat(func.execute(EMPTY)).startsWith("41022320200101000").matches(isValid);
		assertThat(func.execute(EMPTY)).startsWith("41022320200101001").matches(isValid);

		execute(func, 1000);
		assertThat(func.execute(EMPTY)).startsWith("41022320200102002").matches(isValid);

		execute(func, 1000 * 31);
		assertThat(func.execute(EMPTY)).startsWith("41022320200202003").matches(isValid);
	}

	@Test
	public void testJump() {
		Input input = new Input("addressCode", "410223", "startDate", "20200101");
		CitizenIdentification f1 = new CitizenIdentification();
		CitizenIdentification f2 = new CitizenIdentification();

		f1.init(input);
		f2.init(input);

		f1.jump(10);
		execute(f2, 10);
		assertThat(f1.execute(EMPTY)).isEqualTo(f2.execute(EMPTY));

		f1.jump(100);
		execute(f2, 100);
		assertThat(f1.execute(EMPTY)).isEqualTo(f2.execute(EMPTY));

		f1.jump(500);
		execute(f2, 500);
		assertThat(f1.execute(EMPTY)).isEqualTo(f2.execute(EMPTY));
	}

	private void execute(CitizenIdentification func, int count) {
		for (int i = 0; i < count; i++)
			func.execute(Input.EMPTY);
	}
}