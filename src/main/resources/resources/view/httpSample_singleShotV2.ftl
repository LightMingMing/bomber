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
                      style="color:green;min-height: 100px;max-height: 350px;overflow-y: auto">${requestMessage?no_esc}</code>
            </div>
        </div>
    </div>
    <div>
        <div class="control-group">
            <div class="form-inline">
                <label>
                    <input type="number" name="payloadIndex" class="input-small required payload-index" min="0">
                </label>
                <button type="submit" class="btn btn-primary">Execute</button>
            </div>
        </div>
    </div>
</#if>
<#if errorMessage?has_content>
    <div id="error" class="view">
        <div id="control-group-error" class="control-group">
            <label class="control-label" style="font-weight: bold;font-style: italic">错误</label>
            <div class="controls">
                <code class="block json" style="max-height: 350px;overflow-y: auto">${errorMessage?no_esc}</code>
            </div>
        </div>
    </div>
</#if>
</body>
</html>