package com.bomber.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

@FuncInfo(requiredArgs = "length")
public class FixedLengthRandom extends StringFunction {

	// Long.MAX_VALUE length is 19
	private static final int MAX_LENGTH = Long.toString(Long.MAX_VALUE).length();

	private int length;

	private List<Integer> lengthSplits;

	private static Long random(int length) {
		long min = (long) Math.pow(10, length - 1);
		long max = (length == MAX_LENGTH) ? Long.MAX_VALUE : (long) Math.pow(10, length);
		return ThreadLocalRandom.current().nextLong(min, max);
	}

	@Override
	public void init(Input input) {
		this.length = Integer.parseInt(input.get("length"));
		this.lengthSplits = new ArrayList<>(1);

		int temp = this.length;
		for (;;) {
			if (temp > MAX_LENGTH) {
				temp -= MAX_LENGTH;
				lengthSplits.add(MAX_LENGTH);
			} else {
				lengthSplits.add(temp);
				break;
			}
		}
	}

	@Override
	public String execute(Input input) {
		if (this.length <= MAX_LENGTH) {
			return Long.toString(random(this.length));
		} else {
			StringBuilder sb = new StringBuilder();
			lengthSplits.forEach(len -> sb.append(random(len)));
			return sb.toString();
		}
	}
}
