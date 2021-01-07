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
        <div id="control-group-threadGroups" class="control-group">
            <label class="control-label" for="threadGroups" style="width: 120px;"><span
                        data-content="${getText('threadGroups.desc')}"
                        class="poped glyphicon glyphicon-question-sign"></span>${getText('threadGroups')}</label>
            <div class="controls" style="margin-left: 140px;">
                <input type="text" id="threadGroups" name="threadGroups" style="width: 320px;" autocomplete="off"
                       value="${threadGroups!1}">
                <span>&nbsp;✖&nbsp;</span>
                <input type="number" id="iterations" name="iterations" style="width: 50px" min="1" max="500"
                       value="${iterations!1}"/>
                <span data-content="${getText('iterations')}" data-html="true"
                      class="poped glyphicon glyphicon-question-sign"></span>
            </div>
        </div>
        <div id="control-group-requests-per-thread" class="control-group">
            <label class="control-label" for="requestsPerThread" style="width: 120px;"><span
                        data-content="${getText('requestsPerThread.desc')}"
                        data-html="true"
                        class="poped glyphicon glyphicon-question-sign"></span>${getText('requestsPerThread')}
            </label>
            <div class="controls" style="margin-left: 140px;">
                <input type="number" id="requestsPerThread" name="requestsPerThread" maxlength="255" min="1"
                       max="${maxRequestsPerThread!500}" autocomplete="off" value="${requestsPerThread!10}"
                       class="input-small required">
                <span> </span><span id="total-requests">${totalRequests!0}</span>
            </div>
        </div>
        <#if mutable>
            <div id="control-group-begin-user-index" class="control-group">
                <label class="control-label" for="begin-user-index"
                       style="width: 120px;">${getText('beginUserIndex')}</label>
                <div class="controls" style="margin-left: 140px;">
                    <input type="number" id="begin-user-index" name="beginUserIndex"
                           class="input-small required" min="0" value="0">
                </div>
            </div>
            <div id="control-group-scope" class="control-group">
                <label class="control-label" for="scope" style="width: 120px;"><span
                            data-content="${getText('userScope.desc')}"
                            class="poped glyphicon glyphicon-question-sign"></span>${getText('userScope')}
                </label>
                <div class="controls" style="margin-left: 140px;">
                    <select id="scope" name="scope" class="input-medium">
                        <option label="${getText('Request')}" selected>Request</option>
                        <option label="${getText('Thread')}">Thread</option>
                        <option label="${getText('Group')}">Group</option>
                        <option label="${getText('Benchmark')}">Benchmark</option>
                    </select>
                    <span>&nbsp;</span><span id="total-payloads">${totalPayloads!0}</span>
                </div>
            </div>
        </#if>
        <div class="form-actions" style="padding-left: 140px">
            <button type="submit" class="btn btn-primary">${getText('execute')}</button>
        </div>
    </fieldset>
</form>
</body>
</html>