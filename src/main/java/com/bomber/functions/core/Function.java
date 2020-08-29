package com.bomber.functions.core;

import java.io.Closeable;

/**
 * Function
 */
public interface Function<T> extends Closeable, Jumpable {

	void init(Input ctx);

	T execute(Input ctx);

}
