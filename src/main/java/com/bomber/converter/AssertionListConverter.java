package com.bomber.converter;

import java.util.List;

import javax.persistence.Converter;

import org.ironrhino.core.hibernate.convert.JsonConverter;

import com.bomber.model.Assertion;

@Converter
public class AssertionListConverter extends JsonConverter<List<Assertion>> {
}
