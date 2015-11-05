<%@ tag description="Personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.member.Membership" %>
<ul class="subscriptions">
  <c:forEach items="${membership.subscriptions}" var="entry">
      <li class="service">
        <c:out value="${entry.key.value.title}" />
        <a href="#sub-${membership.person.id}-${entry.key.id}-edit">
            Edit
        </a>
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