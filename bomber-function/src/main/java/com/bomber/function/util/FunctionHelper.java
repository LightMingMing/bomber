package com.bomber.function.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bomber.function.Counter;
import com.bomber.function.FuncInfo;
import com.bomber.function.Function;
import com.bomber.function.model.FunctionConstructors;
import com.bomber.function.model.FunctionMetadata;
import com.bomber.function.model.MethodInvoker;

/**
 * 函数工具类
 *
 * @author MingMing Zhao
 */
public final class FunctionHelper {

	private static final Map<String, Class<Function>> functionTypeMap = new LinkedHashMap<>();

	private static final Map<String, FunctionMetadata> functionMetadataMap = new LinkedHashMap<>();

	private static final Map<Class<Function>, FunctionMetadata> functionMetadataForType = new LinkedHashMap<>();

	static {
		FunctionScanner.scan(new String[]{Counter.class.getPackageName()}).forEach(clazz -> {
			String name = generateFunctionName(clazz);
			FuncInfo funcInfo = clazz.getDeclaredAnnotation(FuncInfo.class);
			if (funcInfo != null) {
				FunctionMetadata fm = new FunctionMetadata();
				fm.setFunctionType(clazz);
				fm.setName(name);

				fm.setRequiredArgs(funcInfo.requiredArgs());
				fm.setOptionalArgs(funcInfo.optionalArgs());
				fm.setRetAllArgs(funcInfo.retAllArgs());
				fm.setRetArg(funcInfo.retArg());
				fm.setParallel(funcInfo.parallel());
				fm.setCustomArg(funcInfo.customArg());

				fm.setConstructors(new FunctionConstructors<>(clazz));
				fm.setMethodInvoker(new MethodInvoker(clazz));

				functionTypeMap.put(name, clazz);
				functionMetadataMap.put(name, fm);
				functionMetadataForType.put(clazz, fm);
			}
		});
	}

	private static String generateFunctionName(Class<Function> clazz) {
		return clazz.getSimpleName().replace("Function", "");
	}

	public static Class<Function> getFunctionType(String name) {
		return functionTypeMap.get(requireNonNull(name, "name"));
	}

	public static FunctionMetadata getFunctionMetadata(String name) {
		return functionMetadataMap.get(requireNonNull(name, "name"));
	}

	public static FunctionMetadata getFunctionMetadata(Class<Function> clazz) {
		return functionMetadataForType.get(requireNonNull(clazz, "clazz"));
	}

	public static Collection<FunctionMetadata> getAllFunctionMetadata() {
		return functionMetadataMap.values();
	}

	public static Map<String, String> getLabelValues() {
		Map<String, String> map = new LinkedHashMap<>();
		functionMetadataMap.keySet().forEach(key -> map.put(key, key));
		return Collections.unmodifiableMap(map);
	}

}
