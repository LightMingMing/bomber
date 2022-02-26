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
    <#if httpSample.mutable>
        <div class="form-horizontal">
            <div id="control-group-user-index" class="control-group">
                <label class="control-label" for="from"><span data-content="通过设置用户索引, 使用不同的用户进行测试"
                                                              class="poped glyphicon glyphicon-question-sign"></span>用户索引</label>
                <div class="controls">
                    <input type="number" id="from" name="from"
                           class="input-mini required from" min="0"
                           value="0">
                    <span>-</span>
                    <input type="number" id="to" name="to"
                           class="input-mini required to" min="0"
                           value="0">
                    <button type="button" class="btn btn-primary execute">${getText('execute')}</button>
                </div>
            </div>
        </div>
    <#else>
        <div class="control-group">
            <button type="button" class="btn btn-primary execute">${getText('execute')}</button>
        </div>
    </#if>
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
<div id="responses" class="hidden">
    <div id="control-group" class="control-group">
        <label class="control-label" style="font-weight: bold;font-style: italic">结果</label>
        <div class="controls">
            <div class="errors">
                <code class="block json errors" style="max-height: 350px;overflow-y: auto"></code>
            </div>
            <div class="ok">
                <code class="block json ok" style="max-height: 350px;overflow-y: auto;color:green"></code>
            </div>
        </div>
    </div>
</div>
</body>
</html>