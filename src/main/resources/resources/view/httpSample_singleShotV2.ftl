<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>执行HTTP请求</title>
</head>
<body>
<#if requestMessage?has_content>
    <input type="hidden" class="uid" value="${uid}">
    <div id="request" class="view">
        <div id="control-group-request" class="control-group">
            <label class="control-label" style="font-weight: bold;font-style: italic">请求</label>
            <div class="controls">
                <code class="block json request"
                      style="color:green;max-height: 350px;overflow-y: auto">${requestMessage?no_esc}</code>
            </div>
        </div>
    </div>
    <div>
        <div class="control-group">
            <div class="form-inline">
                <input type="number" name="payloadIndex" class="input-small required payload-index" min="0">
                <button type="button" class="btn btn-primary execute">Execute</button>
            </div>
        </div>
    </div>
    <div id="response" class="hidden">
        <div id="control-group-response" class="control-group">
            <label class="control-label" style="font-weight: bold;font-style: italic">响应</label>
            <div class="controls">
                <code class="block json response"
                      style="color:green;max-height: 350px;overflow-y: auto"></code>
            </div>
        </div>
        <div>
            <div style="float: right">
                <span>耗时<span class="elapsedTimeInMillis"></span>毫秒&nbsp</span>
            </div>
        </div>
    </div>
</#if>
<div id="error" class="<#if !errorMessage?has_content>hidden</#if>">
    <div id="control-group-error" class="control-group">
        <label class="control-label" style="font-weight: bold;font-style: italic">错误</label>
        <div class="controls">
            <code class="block json error"
                  style="max-height: 350px;overflow-y: auto"><#if errorMessage?has_content>${errorMessage?no_esc}</#if></code>
        </div>
    </div>
</div>
</body>
</html>