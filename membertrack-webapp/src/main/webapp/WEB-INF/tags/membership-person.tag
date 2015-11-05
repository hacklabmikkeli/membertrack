<%@ tag description="Personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.member.Membership" %>
<ul class="person">
  <li class="pic">
    <img src="${membership.person.value.gravatarUrl}"
         alt="Avatar" />
  </li>
  <li class="fullname">
    <c:out value="${membership.person.value.fullName}" />
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