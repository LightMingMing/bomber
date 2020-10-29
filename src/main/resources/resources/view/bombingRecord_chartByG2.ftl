<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>图表</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style></style>
    <script src="<@url value='/assets/scripts/bomber.g2.js'/>" type="text/javascript"></script>
    <script src="<@url value='/assets/scripts/g2.min.js'/>" type="text/javascript"></script>
</head>
<body>
<div class="container">
    <div style="text-align: center"><span id="recordName" style="font-weight: bold"></span></div>
    <div style="margin:0 50px 0 50px">
        <p>吞吐量</p>
        <div id="throughput"></div>
    </div>
    <div style="margin:0 50px 0 50px">
        <p>平均响应时间</p>
        <div id="average"></div>
    </div>
</div>
<script>
    const reportsListUrl = '<@url value='/api/summaryReport/list?recordId='/>${recordId!}'
    const recordNameUrl = '<@url value='/api/bombingRecord/getRecordName?id='/>${recordId!}'
    $.get(recordNameUrl, name => $("#recordName").html(name))
    $.get(reportsListUrl, function (data) {
        displaySummaryReports("throughput", "average", data)
    })
</script>
</body>
</html>