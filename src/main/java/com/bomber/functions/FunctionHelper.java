package com.bomber.functions;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.ironrhino.core.struts.I18N;
import org.ironrhino.core.util.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public final class FunctionHelper {

	private static final Logger logger = LoggerFactory.getLogger(FunctionHelper.class);

	private static final Map<String, Class<? extends Function<?>>> functionTypeMap = new LinkedHashMap<>();

	private static final Map<String, FunctionMetadata> functionMetadataMap = new LinkedHashMap<>();

	static {
		ClassScanner.scanAssignable(ClassScanner.getAppPackages(), Function.class).stream()
				.filter(clazz -> !isInterfaceOrAbstract(clazz)).map(clazz -> (Class<? extends Function<?>>) clazz)
				.forEach(clazz -> {
					String name = clazz.getSimpleName().replace("Function", "");
					FunctionMetadata fm;
					try {
						Function<?> instance = clazz.newInstance();
						fm = new FunctionMetadata();
						fm.setFunctionType(clazz);
						fm.setName(name);
						fm.setRequiredArgs(instance.getRequiredArgs());
						fm.setOptionalArgs(instance.getOptionalArgs());
						fm.setOutputAllInputArgs(instance.outputAllInputArgs());
						fm.setOutputArgNames(instance.getOutputArgNames());
						fm.setOutputArgValues(instance.getOutputArgValues());
					} catch (InstantiationException | IllegalAccessException e) {
						logger.warn("skip " + clazz.getName(), e);
						return;
					}
					functionTypeMap.put(name, clazz);
					functionMetadataMap.put(name, fm);
				});
	}

	public static boolean isInterfaceOrAbstract(Class<?> clazz) {
		int mod = clazz.getModifiers();
		return Modifier.isInterface(mod) || Modifier.isAbstract(mod);
	}

	public static Class<? extends Function<?>> getFunctionType(String name) {
		if (name == null)
			return null;
		return functionTypeMap.get(name);
	}

	public static FunctionMetadata getFunctionMetadata(String name) {
		if (name == null)
			return null;
		return functionMetadataMap.get(name);
	}

	public static Map<String, String> getLabelValues() {
		Map<String, String> map = new LinkedHashMap<>();
		functionMetadataMap.keySet().forEach(key -> map.put(key, I18N.getText(key)));
		return Collections.unmodifiableMap(map);
	}

	public static Function<?> instance(String name) throws IllegalAccessException, InstantiationException {
		return instance(name, null);
	}

	public static Function<?> instance(String name, Map<String, String> params)
			throws IllegalAccessException, InstantiationException {
		Class<? extends Function<?>> type = getFunctionType(name);
		Objects.requireNonNull(type);
		Function<?> instance = type.newInstance();
		instance.init(params);
		return instance;
	}
}
