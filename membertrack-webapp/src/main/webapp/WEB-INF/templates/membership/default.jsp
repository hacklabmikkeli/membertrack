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
              <li>
                  <a href="#person-${membership.person.id}-edit">
                    Edit
                  </a>
              </li>
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
    <c:forEach items="${it.memberships}" var="membership">
    <c:set var="personId" value="${membership.person.id}" />
    <div class="popup" id="person-${personId}-edit">
        <form class="pure-form pure-form-aligned">
            <fieldset>
                <legend>Personal information</legend>
                <div class="pure-control-group">
                    <label for="person-${personId}-edit-email">Email</label>
                    <input id="person-${personId}-edit-email"
                           type="text"
                           name="email"
                           value="${membership.person.value.email}" />
                </div>
            </fieldset>
            <fieldset>
                <legend>Phone numbers</legend>
                <c:forEach items="${membership.phoneNumbers}" var="phoneNumber">
                <c:set var="pnId" value="${phoneNumber.id}" />
                <div class="pure-control-group">
                    <label for="person-${personId}-edit-pn-${pnId}"></label>
                    <input id="person-${personId}-edit-pn-${pnId}"
                           type="text"
                           name="phoneNumber"
                           value="${phoneNumber.value.phoneNumber}" />
                </div>
                </c:forEach>
            </fieldset>
            <fieldset>
                <div class="pure-control-group for-buttons">
                    <a class="button pure-button" href="#">
                        Close
                    </a>
                    <button type="submit" class="pure-button">
                        Save
                    </button>
                </div>
            </fieldset>
        </form>
    </div>
    </c:forEach>
</t:master>