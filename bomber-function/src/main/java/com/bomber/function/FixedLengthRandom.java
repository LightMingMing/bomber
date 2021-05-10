package com.bomber.function;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 定长随机数
 *
 * @author MingMing Zhao
 */
@FuncInfo(requiredArgs = "length")
public class FixedLengthRandom implements Producer<String> {

	protected static final int MAX_LENGTH = 100;

	// Long.MAX_VALUE length is 19
	private static final int MAX_LONG_LENGTH = Long.toString(Long.MAX_VALUE).length();

	private final int length;

	private final List<Integer> lengthSplits;

	public FixedLengthRandom(int length) {
		if (length < 1 || length > MAX_LENGTH) {
			throw new IllegalArgumentException("length: " + length + ", expected: (0, " + MAX_LENGTH + "]");
		}
		this.length = length;
		this.lengthSplits = new ArrayList<>(1);

		int temp = length;
		for (; ; ) {
			if (temp > MAX_LONG_LENGTH) {
				temp -= MAX_LONG_LENGTH;
				lengthSplits.add(MAX_LONG_LENGTH);
			} else {
				lengthSplits.add(temp);
				break;
			}
		}
	}

	private static Long random(int length) {
		long min = (long) Math.pow(10, length - 1);
		long max = (length == MAX_LONG_LENGTH) ? Long.MAX_VALUE : (long) Math.pow(10, length);
		return ThreadLocalRandom.current().nextLong(min, max);
	}

	@Override
	public String execute() {
		if (this.length <= MAX_LONG_LENGTH) {
			return Long.toString(random(this.length));
		} else {
			StringBuilder sb = new StringBuilder();
			lengthSplits.forEach(len -> sb.append(random(len)));
			return sb.toString();
		}
	}
}
