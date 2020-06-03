package com.bomber.api.controller;

import com.bomber.api.echart.Position;
import com.bomber.api.echart.Title;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.api.echart.AxisLabel;
import com.bomber.api.echart.Chart;
import com.bomber.api.echart.Legend;
import com.bomber.api.echart.Series;
import com.bomber.api.echart.SeriesType;
import com.bomber.api.echart.XAxis;
import com.bomber.api.echart.YAxis;

@RestController
@RequestMapping("/echart")
public class EchartController {

	@GetMapping("/tps-duration")
	public Chart<Integer, Double> tpsAndAverageDuration() {
		Chart<Integer, Double> chart = new Chart<>();

		Title title = new Title("性能测试结果", "模拟的数据");
		chart.setTitle(title);

		Legend legend = new Legend("TPS", "平均响应时间");
		chart.setLegend(legend);

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		xAxis.add(1, 2, 4, 8, 10, 15, 20, 24, 40, 50);
		chart.setXAxis(xAxis);

		YAxis<Double> tps = new YAxis<>("TPS");
		tps.setMin(0);
		tps.setMax(100);
		tps.setInterval(20);
		tps.setAxisLabel(new AxisLabel("{value}"));
		Series<Double> series = new Series<>();
		series.add(0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0);
		tps.setSeries(series);

		YAxis<Double> duration = new YAxis<>("平均响应时间", Position.RIGHT);
		duration.setMin(0);
		duration.setMin(2);
		duration.setInterval(0.8);
		duration.setAxisLabel(new AxisLabel("{value} s"));
		duration.setPosition(Position.RIGHT);
		series = new Series<>(SeriesType.BAR);
		series.add(0.0, 0.2, 0.4, 0.8, 0.9, 0.10, 0.12, 0.14, 0.16, 0.18);
		duration.setSeries(series);

		chart.addYAxis(tps, duration);
		return chart;
	}
}
