<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>${httpSample.name} - BombingPlan</title>
</head>
<body>
<form action="<@url value="/${entityName}/bomb"/>" method="post"
      class="ajax form-horizontal groupable dontreload" autocomplete="off">
    <fieldset>
        <input type="hidden" id="httpSample_input-httpSample-id" name="httpSample.id" class="id"
               value="${(httpSample.id)!}">
        <div id="control-group-name" class="control-group">
            <label class="control-label" for="name" style="width: 120px;">${getText('name')}</label>
            <div class="controls" style="margin-left: 140px">
                <input type="text" id="name" name="name" maxlength="32" autocomplete="off" style="width: 200px">
            </div>
        </div>
        <div id="control-group-threadGroup" class="control-group">
            <label class="control-label" for="threadGroup" style="width: 120px;"><span
                        data-content="${getText('threadGroup.desc')}"
                        class="poped glyphicon glyphicon-question-sign"/>${getText('threadGroup')}</label>
            <div class="controls" style="margin-left: 140px;">
                <input type="text" id="threadGroup" name="threadGroup" maxlength="255" autocomplete="off"
                       value="${threadGroup!1}" style="width: 400px">
            </div>
        </div>
        <div id="control-group-requests-per-thread" class="control-group">
            <label class="control-label" for="requestsPerThread" style="width: 120px;"><span
                        data-content="${getText('requestsPerThread.desc')}"
                        data-html="true"
                        class="poped glyphicon glyphicon-question-sign"/>${getText('requestsPerThread')}</label>
            <div class="controls" style="margin-left: 140px;">
                <input type="number" id="requestsPerThread" name="requestsPerThread" maxlength="255" min="1"
                       max="${maxRequestsPerThread!500}" autocomplete="off" value="${requestsPerThread!10}"
                       style="width: 40px">
            </div>
        </div>
        <div id="control-group-scope" class="control-group">
            <label class="control-label" for="scope" style="width: 120px;"><span
                        data-content="${getText('payloadScope.desc')}"
                        class="poped glyphicon glyphicon-question-sign"/>${getText('payload')}${getText('scope')}
            </label>
            <div class="controls" style="margin-left: 140px;">
                <select id="scope" name="scope">
                    <option label="${getText('Request')}" selected>Request</option>
                    <option label="${getText('Thread')}">Thread</option>
                    <option label="${getText('Group')}">Group</option>
                    <option label="${getText('Benchmark')}">Benchmark</option>
                </select>
            </div>
        </div>
        <div class="form-actions" style="padding-left: 140px">
            <button type="submit" class="btn btn-primary">${getText('execute')}</button>
        </div>
    </fieldset>
</form>
</body>
</html>