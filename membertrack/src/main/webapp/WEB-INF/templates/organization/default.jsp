<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Organizations">
    <c:forEach items="${it.organizations}" var="organization">
        <c:out value="${organization}" />
    </c:forEach>
</t:master>