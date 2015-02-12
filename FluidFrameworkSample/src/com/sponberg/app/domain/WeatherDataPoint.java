package com.sponberg.app.domain;

import com.sponberg.fluid.layout.TableRowWithId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of={"time"})
public class WeatherDataPoint implements TableRowWithId {

	long time;
	
	String icon;
	
	double precipProbability;
	
	double temperatureMax;
	
	double temperatureMin;

	String summary;
	
	public Double getTemperatureMaxC() {
		return getFtoC(temperatureMax);
	}
	
	public Double getTemperatureMinC() {
		return getFtoC(temperatureMin);
	}
	
	public double getFtoC(double f) {
		return (f - 32) / 1.8;
	}
	
	public Long getTimeInMillis() {
		return time * 1000;
	}

	@Override
	public Long getFluidTableRowObjectId() {
		return time;
	}
	
}
