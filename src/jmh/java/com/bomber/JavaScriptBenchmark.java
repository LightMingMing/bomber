package com.bomber;

import com.bomber.functions.JavaScript;
import com.bomber.functions.core.Input;
import org.ironrhino.core.validation.validators.CitizenIdentificationNumberValidator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class JavaScriptBenchmark {

	String certId = CitizenIdentificationNumberValidator.randomValue();

	Input input = new Input("script", String.format("'%s'.substring(16)", certId));

	JavaScript javaScript = new JavaScript();

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(JavaScriptBenchmark.class.getName()).shouldFailOnError(true).build();
		new Runner(opt).run();
	}

	@Benchmark
	public String measureJavaScriptSubstring() {
		return javaScript.execute(input);
	}

	@Benchmark
	public String baseline() {
		return certId.substring(16);
	}

}
