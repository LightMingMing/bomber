package com.bomber;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.http.HttpHeaders;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 4, time = 2)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class HttpHeadersFormatBenchmark {

	HttpHeaders headers = new HttpHeaders();

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(HttpHeadersFormatBenchmark.class.getName()).shouldFailOnError(true)
				.build();
		new Runner(opt).run();
	}

	@Setup(Level.Trial)
	public void setup() {
		headers.add("Content-Length", "100");
		headers.add("Content-Encoding", "gzip");
		headers.add("X-Powered-By", "Bomber");
		headers.add("X-Request-Id", "3AFlTiXOcCWQbtzPgwSupg");

		headers.add("Content-Type", "application/json");
		headers.add("Content-Type", "text/html;charset=UTF-8");
	}

	@Benchmark
	public String measureFormatByForEach() {
		List<String> headers = new ArrayList<>();
		this.headers.forEach((key, value) -> headers.add(key + ": " + String.join(", ", value)));
		return String.join("\n", headers);
	}

	@Benchmark
	public String measureFormatByEntrySetStream() {
		return headers.entrySet().stream().map(entry -> entry.getKey() + ":" + String.join(", ", entry.getValue()))
				.collect(Collectors.joining("\n"));
	}

	@Benchmark
	public String measureFormatByJoiner() {
		StringJoiner joiner = new StringJoiner("\n");
		headers.forEach((key, value) -> joiner.add(key + ": " + String.join(", ", value)));
		return joiner.toString();
	}

	@Benchmark
	public String measureFormatBySpecialJoiner() {
		StringJoiner joiner = new StringJoiner("\n");
		headers.forEach(
				(key, value) -> joiner.add(key + ": " + (value.size() == 1 ? value.get(0) : String.join(", ", value))));
		return joiner.toString();
	}

}