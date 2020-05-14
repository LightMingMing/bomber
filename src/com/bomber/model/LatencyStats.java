package com.bomber.model;

import lombok.Getter;
import lombok.Setter;
import org.ironrhino.core.util.JsonUtils;

@Getter
@Setter
public class LatencyStats {
    private double max;
    private double avg;
    private double stdDev;
    private Percentiles percentiles;

    @Override
    public String toString() {
        try {
            return JsonUtils.toJson(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}
