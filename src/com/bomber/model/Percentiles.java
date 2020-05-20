package com.bomber.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Percentiles implements Serializable {

	private static final long serialVersionUID = 3871179245795361638L;

	@JsonProperty("0.5")
	private double point50;

	@JsonProperty("0.75")
	private double point75;

	@JsonProperty("0.9")
	private double point90;

	@JsonProperty("0.95")
	private double point95;

	@JsonProperty("0.99")
	private double point99;

}
