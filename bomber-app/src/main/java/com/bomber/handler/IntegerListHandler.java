package com.bomber.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author MingMing Zhao
 */
public class IntegerListHandler implements TypeHandler<List<Integer>> {

	@Override
	public void setParameter(PreparedStatement ps, int i, List<Integer> list, JdbcType jdbcType) throws SQLException {
		Objects.requireNonNull(list);
		StringJoiner joiner = new StringJoiner(", ");
		for (Integer each : list) {
			joiner.add(each.toString());
		}
		ps.setString(i, joiner.toString());
	}

	@Override
	public List<Integer> getResult(ResultSet rs, String columnName) throws SQLException {
		return split(rs.getString(columnName));
	}

	@Override
	public List<Integer> getResult(ResultSet rs, int columnIndex) throws SQLException {
		return split(rs.getString(columnIndex));
	}

	@Override
	public List<Integer> getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return split(cs.getString(columnIndex));
	}

	private List<Integer> split(String content) {
		if (content == null || content.isEmpty()) {
			return Collections.emptyList();
		}
		return Stream.of(content.split(", *")).map(Integer::parseInt)
			.collect(Collectors.toList());
	}
}
