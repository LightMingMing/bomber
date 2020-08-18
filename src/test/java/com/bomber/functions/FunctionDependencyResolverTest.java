package com.bomber.functions;

import static com.bomber.functions.FunctionDependencyResolver.resolveDependencyBySorting;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FunctionDependencyResolverTest {

	@Test
	public void testResolveDependencyBySorting() {
		FunctionOption f1 = new FunctionOption();
		f1.setKey("sum");
		f1.setArgumentValues("a=${a}, b=${b}");

		FunctionOption f2 = new FunctionOption();
		f2.setKey("a");

		FunctionOption f3 = new FunctionOption();
		f3.setKey("c");
		f3.setArgumentValues("a=${sum}, b=${a}");

		FunctionOption f4 = new FunctionOption();
		f4.setKey("b");

		List<FunctionOption> sortedOption = resolveDependencyBySorting(Arrays.asList(f1, f2, f3, f4));

		assertThat(sortedOption).hasSize(4);
		assertThat(sortedOption.get(0)).isEqualTo(f2);
		assertThat(sortedOption.get(1)).isEqualTo(f4);
		assertThat(sortedOption.get(2)).isEqualTo(f1);
		assertThat(sortedOption.get(3)).isEqualTo(f3);
	}

	@Test
	public void testCircularDependency() {
		FunctionOption f1 = new FunctionOption();
		f1.setKey("a");
		f1.setArgumentValues("b=${b}");

		FunctionOption f2 = new FunctionOption();
		f2.setKey("b");
		f2.setArgumentValues("a=${a}");

		assertThatIllegalArgumentException().isThrownBy(() -> resolveDependencyBySorting(Arrays.asList(f1, f2)));
	}

	@Test
	public void testDependentOptions() {
		FunctionOption f1 = new FunctionOption();
		f1.setKey("sum");
		f1.setArgumentValues("a=${a}, b=${b}");

		FunctionOption f2 = new FunctionOption();
		f2.setKey("a");

		FunctionOption f3 = new FunctionOption();
		f3.setKey("c");
		f3.setArgumentValues("a=${sum}, b=${a}");

		FunctionOption f4 = new FunctionOption();
		f4.setKey("b");

		FunctionDependencyResolver resolver = new FunctionDependencyResolver(Arrays.asList(f1, f2, f3, f4));
		List<FunctionOption> dependentOptions;

		dependentOptions = resolver.getDependentOptions(Collections.singleton("a"));
		assertThat(dependentOptions).hasSize(1); // a

		dependentOptions = resolver.getDependentOptions(Collections.singleton("sum"));
		assertThat(dependentOptions).hasSize(3); // a, b, sum

		dependentOptions = resolver.getDependentOptions(Collections.singleton("c"));
		assertThat(dependentOptions).hasSize(4); // a, b, sum, c

	}

}