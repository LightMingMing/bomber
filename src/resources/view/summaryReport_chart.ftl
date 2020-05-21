<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>图表</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style></style>
    <script src="<@url value='/assets/scripts/highcharts.js'/>" type="text/javascript"></script>
</head>
<body>
<div class="container">
    <div id="container" style="margin:50px auto 50px auto"></div>
    <div class="row-fluid">
        <div class="span6 offset3" style="text-align: center">
            <button id="tps" type="button" class="btn">TPS</button>
            <button id="duration-avg" type="button" class="btn">平均响应时间</button>
            <button id="tps-duration" type="button" class="btn">TPS与平均响应时间</button>
            <button id="duration-stats" type="button" class="btn">响应时间分布</button>
            <button id="failure-rate" type="button" class="btn">错误率</button>
        </div>
    </div>
</div>
<script>
    const sampleId = '${sampleId!}';
    console.log("sampleId=" + sampleId);

    function displayChart(chart) {
        console.log(chart)

        xAxis = {
            title: {
                text: chart.xAxis.title
            },
            categories: chart.xAxis.series
        }

        const yAxis = [];
        const series = [];
        let tmp;
        for (let i = 0; i < chart.yAxis.length; i++) {
            tmp = chart.yAxis[i]
            yAxis.push({title: {text: tmp.title}, opposite: tmp.opposite})
            for (let j = 0; j < tmp.series.length; j++) {
                series.push({
                    yAxis: i,
                    name: tmp.series[j].title,
                    data: tmp.series[j].series,
                    type: tmp.series[j].type
                })
            }
        }

        Highcharts.chart('container', {
            title: {
                text: chart.title
            },
            subtitle: {
                text: chart.subTitle
            },
            xAxis: xAxis,
            yAxis: yAxis,
            legend: {
                layout: 'horizontal',
                align: 'center'
            },
            plotOptions: {
                series: {
                    label: {
                        connectorAllowed: false
                    }
                }
            },
            series: series,
            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }
        });
    }

    $("#tps").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/tps?id='/>' + sampleId,
            success: function (chart) {
                displayChart(chart)
            }
        })
    });

    $("#duration-avg").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/duration/avg?id='/>' + sampleId,
            success: function (chart) {
                displayChart(chart)
            }
        })
    });

    $("#tps-duration").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/tps-duration?id='/>' + sampleId,
            success: function (chart) {
                displayChart(chart)
            }
        })
    });

    $("#duration-stats").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/duration/stats?id='/>' + sampleId,
            success: function (chart) {
                displayChart(chart)
            }
        })
    });

    $("#failure-rate").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/failure-rate?id='/>' + sampleId,
            success: function (chart) {
                displayChart(chart)
            }
        })
    });

    $.ajax({
        type: 'get',
        url: '<@url value='/api/chart/tps-duration?id='/>' + sampleId,
        success: function (chart) {
            displayChart(chart)
        }
    })
</script>
</body>
</html>