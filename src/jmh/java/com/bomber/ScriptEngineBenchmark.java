package com.bomber;

import com.bomber.functions.JavaScript;
import com.bomber.functions.JavaScript2;
import com.bomber.functions.MVEL;
import com.bomber.functions.core.Input;
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
public class ScriptEngineBenchmark {

	String str = "0123456";

	Input input1 = new Input("script", String.format("'%s'.substring(3)", str));

	Input input2 = new Input("script", "str.substring(3)", "str", str);

	JavaScript javaScript = new JavaScript();

	JavaScript2 javaScript2 = new JavaScript2();

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
	public String javaScript() {
		return javaScript.execute(input1);
	}

	@Benchmark
	public String javaScript2() {
		return javaScript2.execute(input2);
	}

	@Benchmark
	public String mvel() {
		return mvel.execute(input2);
	}
}
