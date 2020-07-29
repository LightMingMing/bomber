package com.bomber.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.util.function.SingletonSupplier;

public class HttpHeadersUtils {

	static SingletonSupplier<List<String>> headerFieldsSupplier = SingletonSupplier
			.of(HttpHeadersUtils::headerFieldsSupplier);

	public static List<String> getHeaderFields() {
		return headerFieldsSupplier.get();
	}

	private static List<String> headerFieldsSupplier() {
		List<String> headerFields = new ArrayList<>();

		Field[] fields = HttpHeaders.class.getDeclaredFields();

		int mod = Modifier.FINAL | Modifier.STATIC | Modifier.PUBLIC;

		for (Field field : fields) {
			if ((field.getModifiers() & mod) == mod && String.class.isAssignableFrom(field.getType())) {
				try {
					headerFields.add((String) field.get(null));
				} catch (IllegalAccessException e) {
					// ignore
				}
			}
		}
		return headerFields;
	}
}
