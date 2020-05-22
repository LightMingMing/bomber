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
</div>
<script>
    const chartId = "container"
    const bombingIds = '${bombingIds!}';
    console.log("bombingIds=" + bombingIds);

    $.ajax({
        type: 'get',
        url: '<@url value='/api/chart/compare?ids='/>' + bombingIds,
        success: function (chart) {
            displayChart(chartId, chart)
        }
    })
</script>
</body>
</html>