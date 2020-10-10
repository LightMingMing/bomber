package com.bomber.functions.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.bomber.functions.core.FunctionMetadata;
import org.ironrhino.core.util.ClassScanner;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Function;

public final class FunctionHelper {

	private static final Map<String, Class<Function<?>>> functionTypeMap = new LinkedHashMap<>();

	private static final Map<String, FunctionMetadata> functionMetadataMap = new LinkedHashMap<>();

	static {
		FunctionScanner.scan(ClassScanner.getAppPackages()).forEach(clazz -> {
			String name = generateFunctionName(clazz);
			FunctionMetadata fm;

			fm = new FunctionMetadata();
			fm.setFunctionType(clazz);
			fm.setName(name);

			FuncInfo funcInfo = clazz.getDeclaredAnnotation(FuncInfo.class);
			if (funcInfo != null) {
				fm.setRequiredArgs(funcInfo.requiredArgs());
				fm.setOptionalArgs(funcInfo.optionalArgs());
				fm.setRetAllArgs(funcInfo.retAllArgs());
				fm.setRetArg(funcInfo.retArg());
				fm.setParallel(funcInfo.parallel());
			}

			functionTypeMap.put(name, clazz);
			functionMetadataMap.put(name, fm);
		});
	}

	private static String generateFunctionName(Class<Function<?>> clazz) {
		return clazz.getSimpleName().replace("Function", "");
	}

	public static Class<Function<?>> getFunctionType(String name) {
		Objects.requireNonNull(name, "name");
		return functionTypeMap.get(name);
	}

	public static FunctionMetadata getFunctionMetadata(String name) {
		Objects.requireNonNull(name, "name");
		return functionMetadataMap.get(name);
	}

	public static FunctionMetadata getFunctionMetadata(Class<Function<?>> clazz) {
		Objects.requireNonNull(clazz, "clazz");
		return functionMetadataMap.get(generateFunctionName(clazz));
	}

	@SuppressWarnings("unchecked")
	public static FunctionMetadata getFunctionMetadata(Function<?> function) {
		Objects.requireNonNull(function, "function");
		return getFunctionMetadata((Class<Function<?>>) function.getClass());
	}

	public static Map<String, String> getLabelValues() {
		Map<String, String> map = new LinkedHashMap<>();
		functionMetadataMap.keySet().forEach(key -> map.put(key, key));
		return Collections.unmodifiableMap(map);
	}

	public static Function<?> createQuietly(String name) {
		Class<Function<?>> type = getFunctionType(name);
		Objects.requireNonNull(type, "function '" + name + "' not found");
		return createQuietly(type);
	}

	public static Function<?> createQuietly(Class<Function<?>> type) {
		Objects.requireNonNull(type, "type");
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
