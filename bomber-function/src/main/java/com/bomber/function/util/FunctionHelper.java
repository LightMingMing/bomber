package com.bomber.function.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bomber.function.Counter;
import com.bomber.function.FuncInfo;
import com.bomber.function.Function;
import com.bomber.function.Group;
import com.bomber.function.Type;
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
		FunctionScanner.scan(new String[]{Counter.class.getPackageName()}).stream().sorted(FunctionHelper::sort)
			.forEach(clazz -> {
					FunctionMetadata fm = parse(clazz);
					if (fm != null) {
						functionTypeMap.put(fm.getName(), clazz);
						functionMetadataMap.put(fm.getName(), fm);
						functionMetadataForType.put(clazz, fm);
					}
				}
			);
	}

	private static int getOrder(Class<Function> clazz) {
		Group group = clazz.getAnnotation(Group.class);
		return group == null ? Type.OTHER.getOrder() : group.value().getOrder();
	}

	private static int sort(Class<Function> f1, Class<Function> f2) {
		int order1 = getOrder(f1);
		int order2 = getOrder(f2);
		if (order1 == order2)
			return f1.getSimpleName().compareTo(f2.getSimpleName());
		return Integer.compare(order1, order2);
	}

	private static FunctionMetadata parse(Class<Function> clazz) {
		FuncInfo funcInfo = clazz.getDeclaredAnnotation(FuncInfo.class);
		if (funcInfo == null) {
			return null;
		}
		FunctionMetadata fm = new FunctionMetadata();
		fm.setFunctionType(clazz);
		fm.setName(getFunctionName(clazz));

		fm.setRequiredArgs(funcInfo.requiredArgs());
		fm.setOptionalArgs(funcInfo.optionalArgs());
		fm.setRetAllArgs(funcInfo.retAllArgs());
		fm.setRetArg(funcInfo.retArg());
		fm.setParallel(funcInfo.parallel());
		fm.setCustomArg(funcInfo.customArg());

		fm.setConstructors(new FunctionConstructors<>(clazz));
		fm.setMethodInvoker(new MethodInvoker(clazz));
		return fm;
	}

	private static String getFunctionName(Class<Function> clazz) {
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
