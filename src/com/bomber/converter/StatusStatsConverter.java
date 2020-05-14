package com.bomber.converter;

import com.bomber.model.StatusStats;
import org.ironrhino.core.hibernate.convert.JsonConverter;

import javax.persistence.Converter;

@Converter
public class StatusStatsConverter extends JsonConverter<StatusStats> {
}
