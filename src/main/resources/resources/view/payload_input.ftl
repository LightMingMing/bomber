<#ftl output_format='HTML'>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title><#if !entity??><#assign entity=entityName?eval></#if><#assign isnew=!entity??||entity.new/><#assign isnew=!entity??||entity.new/><#if idAssigned><#assign isnew=!entity??||!entity.id?has_content/></#if><#if isnew>${getText('create')}<#else>${getText('edit')}</#if>${getText((richtableConfig.alias?has_content)?then(richtableConfig.alias,entityName))}</title>
</head>
<body>
<form id="${entityName}_input" action="${actionBaseUrl}/save" method="post"
      class="ajax form-horizontal groupable dontreload" autocomplete="off">
    <fieldset>
        <#if !isnew>
            <#if !idAssigned><@s.hidden name="${entityName}.id" class="id"/></#if>
        <#else>
            <#if idAssigned><input type="hidden" name="_isnew" class="disabled-on-success" value="true"/></#if>
        </#if>
        <#if versionPropertyName??><@s.hidden name=entityName+'.'+versionPropertyName class="version"/></#if>

        <div id="control-group-payload-name" class="control-group">
            <label class="control-label" for="payload-name">${getText('name')}</label>
            <div class="controls">
                <input type="text" id="payload-name" name="payload.name" class="required" value="${entity.name!}"
                       maxlength="255" autocomplete="off">
            </div>
        </div>

        <#assign config=uiConfigs['options']/>
        <#assign embeddedUiConfigs=config.embeddedUiConfigs/>
        <div id="control-group-payload-options" class="control-group">
            <label class="control-label">${getText('payloadOptions')}</label>
            <div class="controls">
                <input type="hidden" name="__datagrid_payload.options">
                <table class="table table-bordered table-fixed middle datagrid adaptive required">
                    <thead>
                    <tr>
                        <th>${getText('functionName')}</th>
                        <th>${getText('argumentValues')}</th>
                        <th class="manipulate"><i class="glyphicon glyphicon-plus manipulate add clickable"></i></th>
                    </tr>
                    </thead>
                    <tbody class="ui-sortable">
                    <#assign value=(entity['options'])!/>
                    <#list 0..((value?is_collection&&value?has_content)?then(value?size-1,0)) as index>
                        <#assign option=(entity['options'][index])!/>
                        <tr class="ui-sortable-handle">
                            <td>
                                <div class="form-horizontal">
                                    <div class="control-group">
                                        <label class="control-label" style="width: 50px">${getText('key')}</label>
                                        <div class="controls" style="margin-left: 80px">
                                            <input type="text" name="payload.options[${index}].key" class="required"
                                                   value="${option['key']!}"
                                                   maxlength="255" autocomplete="off">
                                        </div>
                                    </div>
                                    <div class="control-group">
                                        <label class="control-label"
                                               style="width: 50px">${getText('functionName')}</label>
                                        <div class="controls" style="margin-left: 80px">
                                            <#assign fnc=embeddedUiConfigs['functionName']>
                                            <@s.select theme="simple" name="payload.options[${index}].functionName" class=fnc.cssClass list=fnc.listOptions?eval listKey=fnc.listKey listValue=fnc.listValue headerKey="" headerValue=""/>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td>
                            <textarea class="argument-values"
                                      style="height: 120px;overflow-y: auto;font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;"
                                      name="payload.options[${index}].content">${(option['content'])!}</textarea>
                            </td>
                            <td class="manipulate"><i class="glyphicon glyphicon-plus manipulate add clickable"></i>
                                <i class="glyphicon glyphicon-minus manipulate remove clickable"></i></td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存</button>
        </div>
    </fieldset>
</form>
</body>
</html>