<%@ tag description="Master page, template for other pages" 
        pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="sidebar" type="java.lang.Boolean" %>
<%@ attribute name="title" %>
<%@ attribute name="menupos" %>
<%@ attribute name="css" fragment="true" %>
<%@ attribute name="js" fragment="true" %>

<c:url value="/static/css/pure.css"                     var="pureUrl" />
<c:url value="/static/css/pure-theme.css"               var="pureThemeUrl" />
<c:url value="/static/css/membertrack.css"              var="masterCssUrl" />
<c:url value="/static/js/jquery.js"                     var="jQueryUrl" />
<c:url value="/static/js/membertrack.js"                var="masterJsUrl" />

<!DOCTYPE html>
<html class="pure-theme-membertrack">
    <head>
        <title>${title}</title>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="${pureUrl}" />
        <link rel="stylesheet" href="${pureThemeUrl}" />
        <link rel="stylesheet" href="${masterCssUrl}" />
        <jsp:invoke fragment="css" />
    </head>
    <body class="l-page">
        <div class="l-main-area">
            <div class="l-content">
                <jsp:doBody />
            </div>
        </div>
        <c:if test="${empty sidebar or sidebar}">
            <div class="l-sidebar">
                <ul class="main-nav pure-menu-list">
                    <li class="pure-menu-item
                               ${menupos eq 'membership' ? 'selected' : ''}">
                        <a class="pure-menu-link"
                           href="${it.paths.memberships}">
                            Memberships
                        </a>
                    </li>
                </ul>
            </div>
        </c:if>
        <script src="${jQueryUrl}"></script>
        <script src="${masterJsUrl}"></script>
        <jsp:invoke fragment="js" />
    </body>
</html>