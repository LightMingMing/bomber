package com.bomber.functions.util;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bomber.functions.core.FuncInfo;
import com.bomber.functions.core.Function;
import com.bomber.functions.core.FunctionMetadata;

public final class FunctionHelper {

	private static final Map<String, Class<Function<?>>> functionTypeMap = new LinkedHashMap<>();

	private static final Map<String, FunctionMetadata> functionMetadataMap = new LinkedHashMap<>();

	private static final Map<Class<Function<?>>, FunctionMetadata> functionMetadataForType = new LinkedHashMap<>();

	static {
		FunctionScanner.scan(new String[] { "com.bomber.functions" }).forEach(clazz -> {
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
				fm.setCustomArg(funcInfo.customArg());
			}

			functionTypeMap.put(name, clazz);
			functionMetadataMap.put(name, fm);
			functionMetadataForType.put(clazz, fm);
		});
	}

	private static String generateFunctionName(Class<Function<?>> clazz) {
		return clazz.getSimpleName().replace("Function", "");
	}

	public static Class<Function<?>> getFunctionType(String name) {
		return functionTypeMap.get(requireNonNull(name, "name"));
	}

	public static FunctionMetadata getFunctionMetadata(String name) {
		return functionMetadataMap.get(requireNonNull(name, "name"));
	}

	private static FunctionMetadata getFunctionMetadataFromParent(Class<Function<?>> clazz) {
		return functionMetadataForType.keySet().stream().filter(c -> c.isAssignableFrom(clazz)).findFirst()
				.map(functionMetadataForType::get).orElse(null);
	}

	public static FunctionMetadata getFunctionMetadata(Class<Function<?>> clazz) {
		FunctionMetadata metadata = functionMetadataForType.get(requireNonNull(clazz, "clazz"));
		return metadata != null ? metadata : getFunctionMetadataFromParent(clazz);
	}

	@SuppressWarnings("unchecked")
	public static FunctionMetadata getFunctionMetadata(Function<?> function) {
		return getFunctionMetadata((Class<Function<?>>) requireNonNull(function, "function").getClass());
	}

	public static Map<String, String> getLabelValues() {
		Map<String, String> map = new LinkedHashMap<>();
		functionMetadataMap.keySet().forEach(key -> map.put(key, key));
		return Collections.unmodifiableMap(map);
	}

	public static Function<?> createQuietly(String name) {
		return createQuietly(requireNonNull(getFunctionType(name), "function '" + name + "' not found"));
	}

	public static Function<?> createQuietly(Class<Function<?>> type) {
		try {
			return requireNonNull(type, "type").getDeclaredConstructor().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
