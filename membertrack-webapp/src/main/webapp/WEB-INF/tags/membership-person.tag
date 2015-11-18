<%@ tag description="Personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.entity.Entity" %>
<ul class="person">
  <li class="pic">
    <img src="${membership.value.person.gravatarUrl}"
         alt="Avatar" />
  </li>
  <li class="info">
    <ul>
      <li class="fullname">
        <c:out value="${membership.value.person.fullName}" />
      </li>
      <li class="email">
        <c:out value="${membership.value.person.email}" />
      </li>
      <c:forEach items="${membership.value.person.phoneNumbers}"
                 var="phoneNumber">
          <li class="phone-number">
            <c:out value="${phoneNumber.value.phoneNumber}" />
          </li>
      </c:forEach>
      <li>
        <a href="#edit-person-${membership.id}">
          Edit
        </a>
      </li>
    </ul>
  </li>
</ul>