package com.bomber.functions;

import java.util.concurrent.TimeUnit;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Input;
import com.bomber.functions.core.StringFunction;

/**
 * 延迟一段时间, 返回线程名. 仅用来测试
 * 
 * @author zhaomingming
 */
@FuncInfo(requiredArgs = "delay", parallel = true)
public class DelayThreadName extends StringFunction {

	@Override
	public String execute(Input input) {
		try {
			TimeUnit.MILLISECONDS.sleep(Integer.parseInt(input.get("delay")));
		} catch (InterruptedException e) {
			// ignore
		}
		return Thread.currentThread().getName();
	}
}
