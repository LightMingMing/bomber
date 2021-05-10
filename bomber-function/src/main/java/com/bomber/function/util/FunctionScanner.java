package com.bomber.function.util;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import com.bomber.function.Counter;
import com.bomber.function.Function;

/**
 * 函数扫描
 *
 * @author MingMing Zhao
 */
public class FunctionScanner {

	public static boolean isSpecific(Class<?> clazz) {
		int mod = clazz.getModifiers();
		return !(Modifier.isInterface(mod) || Modifier.isAbstract(mod));
	}

	@SuppressWarnings("unchecked")
	public static List<Class<Function>> scan(String[] packages) {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		List<Class<Function>> functions = new LinkedList<>();
		for (String pack : packages) {
			String path = "classpath*:" + pack.replaceAll("\\.", "/") + "/**/*.class";
			try {
				Resource[] resources = resourcePatternResolver.getResources(path);
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
						Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
						if (Function.class.isAssignableFrom(clazz) && isSpecific(clazz)) {
							functions.add((Class<Function>) clazz);
						}
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				// ignore
			}
		}
		return functions;
	}

	public static void main(String[] args) {
		FunctionScanner.scan(new String[]{Counter.class.getPackageName()}).forEach(System.out::println);
	}
}
