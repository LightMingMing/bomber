package com.bomber.converter;

import java.util.List;

import javax.persistence.Converter;

import org.ironrhino.core.hibernate.convert.JsonConverter;

import com.bomber.model.PayloadOption;

@Converter
public class PayloadOptionListConverter extends JsonConverter<List<PayloadOption>> {
}
