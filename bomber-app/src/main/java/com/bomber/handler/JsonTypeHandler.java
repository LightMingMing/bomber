package com.bomber.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.bomber.common.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Java 对象与 Json 转化
 *
 * @author MingMing Zhao
 */
public abstract class JsonTypeHandler<T> extends BaseTypeHandler<T> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, JsonUtils.toJson(parameter));
	}

	@Override
	public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return fromJson(rs.getString(columnName));
	}

	@Override
	public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return fromJson(rs.getString(columnIndex));
	}

	@Override
	public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return fromJson(cs.getString(columnIndex));
	}

	protected T fromJson(String json) {
		try {
			return readValue(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract T readValue(String json) throws JsonProcessingException;
}
