package com.bomber.function;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 函数生成
 *
 * @author MingMing Zhao
 */
public interface FunctionGenerator {

	Map<String, String> generateOne(Integer id, int offset);

	List<Map<String, String>> generateBatch(Integer id, int offset, int size, Collection<String> selectedColumns);

}
