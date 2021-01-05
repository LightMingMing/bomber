<ul class="nav">
    <li><a href="<@url value="/"/>" class="ajax view">${action.getText("index")}</a></li>
    <li><a href="<@url value="/applicationInstance/tabs?tab=env"/>"
           class="ajax view">${action.getText("applicationInstance")}</a></li>
    <li><a href="<@url value="/httpSample"/>" class="ajax view">${action.getText("httpSample")}</a></li>
    <li><a href="<@url value="/bombingRecord"/>" class="ajax view">${action.getText("bombingRecord")}</a></li>
    <li><a href="<@url value="/summaryReport"/>" class="ajax view">${action.getText("summaryReport")}</a></li>
    <li><a href="<@url value="/functionConfigure"/>" class="ajax view">${action.getText("functionConfigure")}</a></li>
    <li><a href="<@url value="/project"/>" class="ajax view">${action.getText("project")}</a></li>
    <li><a href="<@url value="/workbench"/>" class="ajax view">${action.getText("workbench")}</a></li>
    <@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
        <li><a href="<@url value="/common/dictionary"/>" class="ajax view">${action.getText("dictionary")}</a></li>
        <li><a href="<@url value="/user"/>" class="ajax view">${action.getText("user")}</a></li>
    </@authorize>
</ul>