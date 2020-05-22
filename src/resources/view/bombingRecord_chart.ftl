<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>图表</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style></style>
    <script src="<@url value='/assets/scripts/highcharts.js'/>" type="text/javascript"></script>
    <script src="<@url value='/assets/scripts/displaychart.js'/>" type="text/javascript"></script>
</head>
<body>
<div class="container">
    <div id="container" style="margin:50px auto 50px auto"></div>
    <div class="row-fluid">
        <div class="span6 offset3" style="text-align: center">
            <button id="tps-duration" type="button" class="btn">TPS与平均响应时间</button>
            <button id="duration-stats" type="button" class="btn">响应时间分布</button>
        </div>
    </div>
</div>
<script>
    const chartId = "container"
    const bombingId = '${bombingId!}';
    console.log("bombingId=" + bombingId);

    $("#tps-duration").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/tps-duration?id='/>' + bombingId,
            success: function (chart) {
                displayChart(chartId, chart)
            }
        })
    });

    $("#duration-stats").click(function () {
        $.ajax({
            type: 'get',
            url: '<@url value='/api/chart/duration/stats?id='/>' + bombingId,
            success: function (chart) {
                displayChart(chartId, chart)
            }
        })
    });

    $.ajax({
        type: 'get',
        url: '<@url value='/api/chart/tps-duration?id='/>' + bombingId,
        success: function (chart) {
            displayChart(chartId, chart)
        }
    })
</script>
</body>
</html>