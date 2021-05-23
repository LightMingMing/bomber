package com.bomber.engine.util;

/**
 * @author MingMing Zhao
 */
public class ConstantException extends Throwable {

	private static final long serialVersionUID = -4818525783866063872L;

	public ConstantException(String message) {
		super(message, null, false, false);
	}
}
