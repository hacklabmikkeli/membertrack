<%-- 
    Document   : default
    Created on : Oct 26, 2015, 4:39:03 PM
    Author     : Ilmo Euro
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:master title="Memberships" menupos="membership">
    <ul class="subscriber-list">
      <c:forEach items="${it.memberships}" var="membership">
          <li>
            <ul class="personal">
              <li class="pic">
                <img src="${membership.person.value.gravatarUrl}"
                     alt="Avatar" />
              </li>
              <li class="email">
                <c:out value="${membership.person.value.email}" />
              </li>
              <c:forEach items="${membership.phoneNumbers}"
                         var="phoneNumber">
                  <li class="phone-number">
                    <c:out value="${phoneNumber.value.phoneNumber}" />
                  </li>
              </c:forEach>
            </ul>
            <ul class="subscriptions">
              <c:forEach items="${membership.subscriptions}" var="entry">
                  <li class="service">
                    <c:out value="${entry.key.value.title}" />
                  </li>
                  <c:forEach items="${entry.value}"
                             var="subscription">
                  <li>
                    <ul>
                      <li class="date">
                        <c:out value="${subscription.value.start_fi_FI}" />
                      </li>
                      <li class="date">
                        <c:out value="${subscription.value.end_fi_FI}" />
                      </li>
                      <li class="payment">
                        <c:out value="${subscription.value.paymentFormatted}" />
                        &euro;
                      </li>
                    </ul>
                  </li>
                  </c:forEach>
              </c:forEach>
            </ul>
          </li>
      </c:forEach>
    </ul>
</t:master>