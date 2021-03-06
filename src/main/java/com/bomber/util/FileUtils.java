package com.bomber.util;

import org.springframework.util.Assert;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class FileUtils {

	public static String readSpecificLine(InputStream stream, int lineNumber) throws IOException {
		Objects.requireNonNull(stream, "stream");
		Assert.isTrue(lineNumber >= 0, "Line number must be a non-negative value");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF_8))) {
			int count = 0;
			for (;;) {
				String line = reader.readLine();
				if (line == null) {
					throw new IllegalArgumentException(
							String.format("Line number %d exceeds max line number %d", lineNumber, count));
				}
				if (count++ == lineNumber) {
					return line;
				}
			}
		}
	}

}
