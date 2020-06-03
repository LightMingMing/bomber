package com.bomber.api.controller;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.client.utils.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bomber.api.model.Axis;
import com.bomber.api.model.Chart;
import com.bomber.api.model.ChartType;
import com.bomber.api.model.XAxis;
import com.bomber.api.model.YAxis;
import com.bomber.manager.BombingRecordManager;
import com.bomber.manager.SummaryReportManager;
import com.bomber.model.BombingRecord;
import com.bomber.model.HttpSample;
import com.bomber.model.SummaryReport;

@RestController
@RequestMapping("/chart")
public class ChartController {

	private final BombingRecordManager bombingRecordManager;

	private final SummaryReportManager summaryReportManager;

	public ChartController(BombingRecordManager bombingRecordManager, SummaryReportManager summaryReportManager) {
		this.bombingRecordManager = bombingRecordManager;
		this.summaryReportManager = summaryReportManager;
	}

	private static Comparator<SummaryReport> comparingNumberOfThreads() {
		return Comparator.comparingInt(SummaryReport::getNumberOfThreads);
	}

	private String subTitleFromDate() {
		return DateUtils.formatDate(new Date(), "yyyy年MM月dd日");
	}

	private String title(BombingRecord bombingRecord, String suffix) {
		return Optional.ofNullable(bombingRecord.getHttpSample()).map(HttpSample::getName)
				.map(prefix -> prefix + "-" + suffix).orElse(suffix);
	}

	@GetMapping("/tps-duration")
	public Chart<Integer, Double> tpsAndAverageDuration(String id) {
		BombingRecord bombingRecord = bombingRecordManager.get(id);

		if (bombingRecord == null) {
			throw new IllegalArgumentException("bombingRecord does not exist");
		}

		String title = title(bombingRecord, "TPS和平均响应时间与并发数变化关系");
		Chart<Integer, Double> chart = new Chart<>(title, subTitleFromDate());

		String name = bombingRecord.getName();

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> tpsYAxis = new YAxis<>("TPS (r/s)");
		YAxis<Double> durationYAxis = new YAxis<>("平均响应时间(ms)", true);

		Axis<Double> tps = new Axis<>(name + " TPS", ChartType.LINE);
		Axis<Double> avg = new Axis<>(name + " AVG", ChartType.LINE);
		durationYAxis.add(avg);
		tpsYAxis.add(tps);

		summaryReportManager.listByBombingRecord(id).stream().sorted(comparingNumberOfThreads()).forEach(summary -> {
			xAxis.add(summary.getNumberOfThreads());
			tps.add(summary.getTps());
			avg.add(summary.getAvg());
		});

		chart.setAxis(xAxis, tpsYAxis, durationYAxis);
		return chart;
	}

	@GetMapping("/duration/stats")
	public Chart<Integer, Double> durationStats(String id) {
		BombingRecord bombingRecord = bombingRecordManager.get(id);

		if (bombingRecord == null) {
			throw new IllegalArgumentException("bombingRecord does not exist");
		}

		String title = title(bombingRecord, "响应时间分布");
		Chart<Integer, Double> chart = new Chart<>(title, subTitleFromDate());

		String name = bombingRecord.getName();

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> durationYAxis = new YAxis<>("响应时间(ms)");

		Axis<Double> point50 = new Axis<>(name + " 50%", ChartType.LINE);
		Axis<Double> point75 = new Axis<>(name + " 75%", ChartType.LINE);
		Axis<Double> point90 = new Axis<>(name + " 90%", ChartType.LINE);
		Axis<Double> point95 = new Axis<>(name + " 95%", ChartType.LINE);
		Axis<Double> point99 = new Axis<>(name + " 99%", ChartType.LINE);
		durationYAxis.add(point50, point75, point90, point95, point99);

		summaryReportManager.listByBombingRecord(id).stream().sorted(comparingNumberOfThreads()).forEach(summary -> {
			xAxis.add(summary.getNumberOfThreads());
			point50.add(summary.getPoint50());
			point75.add(summary.getPoint75());
			point90.add(summary.getPoint90());
			point95.add(summary.getPoint95());
			point99.add(summary.getPoint99());
		});

		chart.setAxis(xAxis, durationYAxis);
		return chart;
	}

	@GetMapping("/compare")
	public Chart<Integer, Double> compare(String ids) {
		// TODO polish
		List<BombingRecord> bombingRecords = bombingRecordManager.get(Arrays.asList(ids.split(", *"))).stream()
				.filter(Objects::nonNull).collect(Collectors.toList());

		// TODO httpSample name
		String title = "TPS和平均响应时间与并发数变化关系";
		Chart<Integer, Double> chart = new Chart<>(title, subTitleFromDate());

		XAxis<Integer> xAxis = new XAxis<>("并发数");
		YAxis<Double> tpsYAxis = new YAxis<>("TPS (r/s)");
		YAxis<Double> durationYAxis = new YAxis<>("平均响应时间(ms)", true);

		// 交集
		Set<Integer> threads = bombingRecords.stream().map(BombingRecord::getThreadGroup).map(HashSet::new)
				.reduce((set1, set2) -> {
					set1.retainAll(set2);
					return set1;
				}).orElse(new HashSet<>());

		boolean threadsAdded = false;
		for (BombingRecord bombingRecord : bombingRecords) {
			Axis<Double> tps = new Axis<>(bombingRecord.getName() + " TPS", ChartType.LINE);
			Axis<Double> avg = new Axis<>(bombingRecord.getName() + " AVG", ChartType.LINE);

			boolean finalThreadsAdded = threadsAdded;

			summaryReportManager.listByBombingRecord(bombingRecord.getId()).stream()
					.filter(summary -> threads.contains(summary.getNumberOfThreads()))
					.sorted(comparingNumberOfThreads()).forEach(summary -> {
						if (!finalThreadsAdded) {
							xAxis.add(summary.getNumberOfThreads());
						}
						tps.add(summary.getTps());
						avg.add(summary.getAvg());
					});
			durationYAxis.add(avg);
			tpsYAxis.add(tps);
			threadsAdded = true;
		}

		chart.setAxis(xAxis, tpsYAxis, durationYAxis);
		return chart;
	}
}
