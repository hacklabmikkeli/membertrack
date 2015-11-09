<%-- 
    Document   : default
    Created on : Oct 26, 2015, 4:39:03 PM
    Author     : Ilmo Euro
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Memberships" menupos="membership">
    <c:set var="myUrl"
           value="${requestScope['javax.servlet.forward.request_uri']}" />
    <div class="pure-menu pure-menu-horizontal">
        <ul class="pure-menu-list">
        <c:forEach begin="1" end="${it.numPages}" var="pageNum">
            <li class="pure-menu-item
                       ${it.currentPage == pageNum ? 'pure-menu-selected' : ''}">
                <a class="pure-menu-link"
                   href="${it.paths.memberships}${pageNum}">
                  <c:out value="${pageNum}" />
                </a>
            </li>
        </c:forEach>
        </ul>
    </div>
    <ul class="subscriber-list">
      <c:forEach items="${it.memberships}" var="membership">
          <li>
            <t:membership-person membership="${membership}" />
            <t:membership-subscriptions membership="${membership}" />
          </li>
      </c:forEach>
    </ul>
    <c:forEach items="${it.memberships}" var="membership">
        <t:membership-person-edit
            membership="${membership}"
            personsUrl="${it.paths.persons}"
            gotoUrl="${myUrl}" />
        <t:membership-subscriptions-edit
            membership="${membership}"
            gotoUrl="${myUrl}" />
    </c:forEach>
</t:master>