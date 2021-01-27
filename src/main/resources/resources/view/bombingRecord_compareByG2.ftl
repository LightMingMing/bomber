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
    <div style="margin:50px 50px 50px 50px">
        <p>吞吐量</p>
        <div id="throughput"></div>
    </div>
</div>
<script>
    const recordIds = '${recordIds!}'.split(/,\s*/);

    function reportListUrl(id) {
        return '<@url value='/api/summaryReports?recordId='/>' + id;
    }

    function recordNameUrl(id) {
        return '<@url value='/api/bombingRecords/'/>' + id + '/name';
    }

    let records = []
    $.ajaxSettings.async = false
    recordIds.forEach(id => $.get(reportListUrl(id), data => records.push(preprocess(data))))
    records = getIntersectionOnCommonThreads(records)
    recordIds.forEach((id, i) => $.get(recordNameUrl(id), name => records[i].forEach(r => r.name = name)))
    $.ajaxSettings.async = true

    // union
    let data = []
    records.forEach(record => record.forEach(r => data.push(r)))
    displayCompareChart("throughput", data)
</script>
< /body>
< /html>