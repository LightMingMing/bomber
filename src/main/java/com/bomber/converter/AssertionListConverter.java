package com.bomber.converter;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.ironrhino.core.util.JsonUtils;

import com.bomber.model.Assertion;
import com.fasterxml.jackson.core.type.TypeReference;

@Converter
public class AssertionListConverter implements AttributeConverter<List<Assertion>, String> {

	@Override
	public String convertToDatabaseColumn(List<Assertion> list) {
		if (list == null) {
			return null;
		}
		return JsonUtils.toJson(list);
	}

	@Override
	public List<Assertion> convertToEntityAttribute(String s) {
		if (s == null) {
			return null;
		}
		try {
			return JsonUtils.fromJson(s, new TypeReference<List<Assertion>>() {
			});
		} catch (IOException e) {
			throw new IllegalArgumentException("Illegal json", e);
		}
	}

}
