package com.bomber.converter;

import com.bomber.model.LatencyStats;
import org.ironrhino.core.hibernate.convert.JsonConverter;

import javax.persistence.Converter;

@Converter
public class LatencyStatsConverter extends JsonConverter<LatencyStats> {
}
