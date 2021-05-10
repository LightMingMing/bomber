package com.bomber.function;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author MingMing Zhao
 */
class CitizenIdentificationTest {

	@Test
	public void testExecute() {
		CitizenIdentification identification = new CitizenIdentification();

		assertThat(identification.execute()).startsWith("41022319950101000");
		assertThat(identification.execute()).startsWith("41022319950101001");

		execute(identification, 1000);
		assertThat(identification.execute()).startsWith("41022319950102002");

		execute(identification, 1000 * 31);
		assertThat(identification.execute()).startsWith("41022319950202003");
	}

	@Test
	public void testJump() {
		CitizenIdentification f1 = new CitizenIdentification("410223", "20200101");
		CitizenIdentification f2 = new CitizenIdentification("410223", "20200101");

		f1.jump(10);
		execute(f2, 10);

		assertThat(f1.execute()).isEqualTo(f2.execute());

		f1.jump(100);
		execute(f2, 100);
		assertThat(f1.execute()).isEqualTo(f2.execute());

		f1.jump(500);
		execute(f2, 500);
		assertThat(f1.execute()).isEqualTo(f2.execute());
	}

	private void execute(CitizenIdentification func, int count) {
		for (int i = 0; i < count; i++)
			func.execute();
	}
}