<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>${httpSample.name} - 测试计划</title>
</head>
<body>
<form action="<@url value="/${entityName}/benchmark"/>" method="post"
      class="ajax form-horizontal groupable dontreload" autocomplete="off">
    <fieldset>
        <input type="hidden" id="httpSample_input-httpSample-id" name="httpSample.id" class="id"
               value="${(httpSample.id)!}">
        <div id="control-group-threadGroup" class="control-group">
            <label class="control-label" for="threadGroup">线程组</label>
            <div class="controls">
                <input type="text" id="threadGroup" name="threadGroup" maxlength="255" autocomplete="off" width="200px"
                       value="${threadGroup!1}" style="width: 400px">
            </div>
        </div>
        <div id="control-group-requests-per-thread" class="control-group">
            <label class="control-label" for="requestsPerThread">请求数/线程</label>
            <div class="controls">
                <input type="number" id="requestsPerThread" name="requestsPerThread" maxlength="255" min="1" max="50"
                       autocomplete="off" value="${requestsPerThread!10}" style="width: 35px">
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">执行</button>
        </div>
    </fieldset>
</form>
</body>
</html>