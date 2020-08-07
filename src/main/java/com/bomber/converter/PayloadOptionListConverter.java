package com.bomber.converter;

import java.io.IOException;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.bomber.model.PayloadOption;
import org.ironrhino.core.util.JsonUtils;

import com.fasterxml.jackson.core.type.TypeReference;

@Converter
public class PayloadOptionListConverter implements AttributeConverter<List<PayloadOption>, String> {

	@Override
	public String convertToDatabaseColumn(List<PayloadOption> list) {
		if (list == null) {
			return null;
		}
		return JsonUtils.toJson(list);
	}

	@Override
	public List<PayloadOption> convertToEntityAttribute(String s) {
		if (s == null) {
			return null;
		}
		try {
			return JsonUtils.fromJson(s, new TypeReference<List<PayloadOption>>() {
			});
		} catch (IOException e) {
			throw new IllegalArgumentException("Illegal json", e);
		}
	}

}
