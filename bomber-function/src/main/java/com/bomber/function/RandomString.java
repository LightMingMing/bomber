package com.bomber.function;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机字符串
 *
 * @author ZhaoMingMing
 */
@Group(Type.BASE)
@FuncInfo(requiredArgs = "length", parallel = true)
public class RandomString implements Producer<String> {

	private static final char[] candidates = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

	private final int length;

	public RandomString(int length) {
		this.length = length;
	}

	@Override
	public String execute() {
		Random random = ThreadLocalRandom.current();
		char[] arr = new char[length];
		for (int i = 0; i < length; i++) {
			arr[i] = candidates[random.nextInt(candidates.length)];
		}
		return new String(arr);
	}

}