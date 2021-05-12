package com.bomber;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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

import com.bomber.function.MVEL;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 3, time = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ScriptEngineBenchmark {

	String str = "0123456";

	String script = "str.substring(3)";

	Map<String, String> parameterValues = Map.of("str", str);

	MVEL mvel = new MVEL();

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(ScriptEngineBenchmark.class.getName()).shouldFailOnError(true)
				.build();
		new Runner(opt).run();
	}

	@Benchmark
	public String baseline() {
		return str.substring(3);
	}

	@Benchmark
	public String mvel() {
		return mvel.execute(script, parameterValues);
	}
}
