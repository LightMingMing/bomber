<ul class="nav">
    <li><a href="<@url value="/"/>" class="ajax view">${action.getText("index")}</a></li>
    <li><a href="<@url value="/httpSample"/>" class="ajax view">${action.getText("httpSample")}</a></li>
    <li><a href="<@url value="/summaryReport"/>" class="ajax view">${action.getText("summaryReport")}</a></li>
    <@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
        <li><a href="<@url value="/user"/>" class="ajax view">${action.getText("user")}</a></li>
    </@authorize>
</ul>