package com.bomber.api.controller;

import java.util.Comparator;
import java.util.Date;

import com.bomber.model.SummaryReport;
import org.apache.http.client.utils.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.api.model.Axis;
import com.bomber.api.model.Chart;
import com.bomber.api.model.ChartType;
import com.bomber.api.model.XAxis;
import com.bomber.api.model.YAxis;
import com.bomber.manager.HttpSampleManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.HttpSample;

@RestController
@RequestMapping("/chart")
public class ChartController {

	private final HttpSampleManager httpSampleManager;

	private final SummaryReportManager summaryReportManager;

	public ChartController(HttpSampleManager httpSampleManager, SummaryReportManager summaryReportManager) {
		this.httpSampleManager = httpSampleManager;
		this.summaryReportManager = summaryReportManager;
	}

	private static Comparator<SummaryReport> comparingNumberOfThreads() {
		return Comparator.comparingInt(SummaryReport::getNumberOfThreads);
	}

	private static String subTitleFromDate() {
		return DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
	}

	private String getSampleName(String id) {
		HttpSample httpSample;
		return (httpSample = httpSampleManager.get(id)) == null ? null : httpSample.getName();
	}

	@GetMapping("/tps")
	public Chart<Integer, Double> tps(String id) {
		Chart<Integer, Double> chart = new Chart<>("平均响应时间与并发数变化关系", subTitleFromDate());

		String name = getSampleName(id);
		if (name == null)
			return chart;

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> tpsYAxis = new YAxis<>("TPS (r/s)");

		Axis<Double> tps = new Axis<>(name + " TPS");
		tpsYAxis.add(tps);

		summaryReportManager.listByHttpSample(id).stream().sorted(comparingNumberOfThreads()).forEach(record -> {
			xAxis.add(record.getNumberOfThreads());
			tps.add(record.getTps());
		});

		chart.setAxis(xAxis, tpsYAxis);
		return chart;
	}

	@GetMapping("/duration/avg")
	public Chart<Integer, Double> averageDuration(String id) {
		Chart<Integer, Double> chart = new Chart<>("平均响应时间与并发数变化关系", subTitleFromDate());

		String name = getSampleName(id);
		if (name == null)
			return chart;

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> durationYAxis = new YAxis<>("平均响应时间(ms)");

		Axis<Double> avg = new Axis<>(name + " AVG", ChartType.LINE);
		durationYAxis.add(avg);

		summaryReportManager.listByHttpSample(id).stream().sorted(comparingNumberOfThreads()).forEach(record -> {
			xAxis.add(record.getNumberOfThreads());
			avg.add(record.getAvg());
		});

		chart.setAxis(xAxis, durationYAxis);
		return chart;
	}

	@GetMapping("/tps-duration")
	public Chart<Integer, Double> tpsAndAverageDuration(String id) {
		Chart<Integer, Double> chart = new Chart<>("TPS和平均响应时间与并发数变化关系", subTitleFromDate());

		String name = getSampleName(id);
		if (name == null)
			return chart;

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> tpsYAxis = new YAxis<>("TPS (r/s)");
		YAxis<Double> durationYAxis = new YAxis<>("平均响应时间(ms)", true);

		Axis<Double> tps = new Axis<>(name + " TPS", ChartType.LINE);
		Axis<Double> avg = new Axis<>(name + " AVG", ChartType.LINE);
		durationYAxis.add(avg);
		tpsYAxis.add(tps);

		summaryReportManager.listByHttpSample(id).stream().sorted(comparingNumberOfThreads()).forEach(record -> {
			xAxis.add(record.getNumberOfThreads());
			tps.add(record.getTps());
			avg.add(record.getAvg());
		});

		chart.setAxis(xAxis, tpsYAxis, durationYAxis);
		return chart;
	}

	@GetMapping("/duration/stats")
	public Chart<Integer, Double> durationStats(String id) {
		Chart<Integer, Double> chart = new Chart<>("响应时间分布", subTitleFromDate());

		String name = getSampleName(id);
		if (name == null)
			return chart;

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> durationYAxis = new YAxis<>("响应时间(ms)");

		Axis<Double> point50 = new Axis<>(name + " 50%", ChartType.LINE);
		Axis<Double> point75 = new Axis<>(name + " 75%", ChartType.LINE);
		Axis<Double> point90 = new Axis<>(name + " 90%", ChartType.LINE);
		Axis<Double> point95 = new Axis<>(name + " 95%", ChartType.LINE);
		Axis<Double> point99 = new Axis<>(name + " 99%", ChartType.LINE);
		durationYAxis.add(point50, point75, point90, point95, point99);

		summaryReportManager.listByHttpSample(id).stream().sorted(comparingNumberOfThreads()).forEach(record -> {
			xAxis.add(record.getNumberOfThreads());
			point50.add(record.getPoint50());
			point75.add(record.getPoint75());
			point90.add(record.getPoint90());
			point95.add(record.getPoint95());
			point99.add(record.getPoint99());
		});

		chart.setAxis(xAxis, durationYAxis);
		return chart;
	}

	@GetMapping("/failure-rate")
	public Chart<Integer, Double> failureRate(String id) {
		Chart<Integer, Double> chart = new Chart<>("错误率", subTitleFromDate());

		String name = getSampleName(id);
		if (name == null)
			return chart;
		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> errorRateYAxis = new YAxis<>("错误率");

		Axis<Double> errorRate = new Axis<>(name + " 错误率", ChartType.COLUMN);
		errorRateYAxis.add(errorRate);

		summaryReportManager.listByHttpSample(id).stream().sorted(comparingNumberOfThreads()).forEach(record -> {
			xAxis.add(record.getNumberOfThreads());
			errorRate.add(1 - record.getSuccessRate());
		});

		chart.setXAxis(xAxis);
		chart.addYAxis(errorRateYAxis);
		return chart;
	}

}
