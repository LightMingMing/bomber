package com.bomber.converter;

import java.util.List;

import javax.persistence.Converter;

import org.ironrhino.core.hibernate.convert.JsonConverter;

import com.bomber.model.FunctionDefinition;

@Converter
public class FunctionDefinitionListConverter extends JsonConverter<List<FunctionDefinition>> {
}
