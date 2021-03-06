<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>${getText('preview')}</title>
</head>
<body>
<#if content?has_content>
    <pre style="max-height: 500px;overflow-y: auto">${content}</pre>
    <form class="form-horizontal" action="<@url value="/${entityName}/download"/>" method="get">
        <input type="hidden" id="functionConfigure-id" name="id" class="id"
               value="${functionConfigure.id}">
        <div class="control-group">
            <label class="control-label">选择</label>
            <div class="controls">
                <#list columns as column>
                    <label class="checkbox inline">
                        <input type="checkbox" value="${column}" name="columns" checked>${column}
                    </label>
                </#list>
            </div>
        </div>
        <div class="row-fluid">
            <div class="span4">
                <div class="control-group">
                    <label class="control-label" for="rows">行数</label>
                    <div class="controls">
                        <input type="text" id="rows" class="input-small" name="rows" value="1000">
                    </div>
                </div>
            </div>
            <div class="span4">
                <div class="control-group">
                    <label class="control-label" for="delimiter">分隔符-默认'${delimiter}'</label>
                    <div class="controls">
                        <input type="text" id="delimiter" class="input-small" name="delimiter" value="${delimiter}">
                    </div>
                </div>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">${getText('download')}</button>
        </div>
    </form>
</#if>
</body>
</html>