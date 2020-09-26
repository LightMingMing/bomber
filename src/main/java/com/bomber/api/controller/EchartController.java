package com.bomber.api.controller;

import static com.bomber.util.NumberUtils.reserveUpMaxBit;

import java.util.Comparator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.api.echart.Chart;
import com.bomber.api.echart.Legend;
import com.bomber.api.echart.Position;
import com.bomber.api.echart.Series;
import com.bomber.api.echart.Title;
import com.bomber.api.echart.XAxis;
import com.bomber.api.echart.YAxis;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.SummaryReport;

@RestController
@RequestMapping("/echart")
public class EchartController {

	private final BombingRecordManager bombingRecordManager;

	private final SummaryReportManager summaryReportManager;

	public EchartController(BombingRecordManager bombingRecordManager, SummaryReportManager summaryReportManager) {
		this.bombingRecordManager = bombingRecordManager;
		this.summaryReportManager = summaryReportManager;
	}

	private static Comparator<SummaryReport> comparingNumberOfThreads() {
		return Comparator.comparingInt(SummaryReport::getNumberOfThreads);
	}

	@GetMapping("/tps-duration")
	public Chart<Integer, Double> tpsAndAverageDuration(String id) {
		BombingRecord bombingRecord = bombingRecordManager.get(id);

		if (bombingRecord == null) {
			throw new IllegalArgumentException("bombingRecord does not exist");
		}

		Chart<Integer, Double> chart = new Chart<>();

		Title title = new Title(bombingRecord.getHttpSample().getName());
		chart.setTitle(title);

		Legend legend = new Legend("TPS", "平均响应时间");
		chart.setLegend(legend);

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		chart.setXAxis(xAxis);

		YAxis tps = new YAxis("TPS", "{value}");
		YAxis duration = new YAxis("平均响应时间", "{value} ms", Position.RIGHT);
		chart.addYAxis(tps, duration);

		Series<Double> tpsSeries = new Series<>(0);
		Series<Double> durationSeries = new Series<>(1);
		summaryReportManager.list(id).stream().sorted(comparingNumberOfThreads()).forEach(summary -> {
			xAxis.add(summary.getNumberOfThreads());
			tpsSeries.add(summary.getTps());
			durationSeries.add(summary.getAvg());
		});
		chart.addSeries(tpsSeries, durationSeries);

		tpsSeries.getData().stream().max(Double::compareTo).ifPresent(max -> {
			tps.setInterval(reserveUpMaxBit(max / 5));
			tps.setMax(tps.getInterval() * 5);
		});

		durationSeries.getData().stream().max(Double::compareTo).ifPresent(max -> {
			duration.setInterval(reserveUpMaxBit(max / 5));
			duration.setMax(duration.getInterval() * 5);
		});

		chart.addYAxis(tps, duration);

		return chart;
	}
}
