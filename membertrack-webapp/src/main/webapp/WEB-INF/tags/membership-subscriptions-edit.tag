<%@ tag description="Personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.entity.Entity" %>
<%@ attribute name="gotoUrl" type="java.lang.String" %>
<c:set var="personId" value="${membership.id}" />
<c:forEach items="${membership.value.subscriptions}" var="entry">
    <c:set var="subId" value="${entry.key.id}" />
    <div class="popup hide-nontarget" id="edit-sub-${personId}-${subId}">
      <form class="pure-form pure-form-aligned">
        <input type="hidden" name="personId" value="${personId}" />
        <input type="hidden" name="subId" value="${subId}" />
        <input type="hidden" name="goto" value="${gotoUrl}" />
        <fieldset>
          <legend>
            <c:out value="${membership.value.person.fullName}" />
            &mdash;
            <c:out value="${entry.key.value.title}" />
          </legend>
          <c:forEach items="${entry.value}" var="subscription">
              <div class="pure-control-group">
                <input type="text"
                       style="width: 8em;"
                       name="startDate"
                       size="8"
                       value="${subscription.value.start_fi_FI}" />
                &mdash;
                <input type="text"
                       style="width: 8em;"
                       name="endDate"
                       size="8"
                       value="${subscription.value.end_fi_FI}" />
                <span style="display: inline-block; width: 1em;">&nbsp;</span>
                <input type="text"
                       style="width: 8em;"
                       name="payment"
                       size="8"
                       value="${subscription.value.paymentFormatted}" />
                &euro;
                <button class="pure-button" data-destroy="{parent}">Remove</button>
              </div>
          </c:forEach>
          <div class="pure-control-group hide-prototype proto-sub-new">
            <input type="text"
                   style="width: 8em;"
                   name="startDate"
                   size="8"
                   value="" />
            &mdash;
            <input type="text"
                   style="width: 8em;"
                   name="endDate"
                   size="8"
                   value="" />
            <span style="display: inline-block; width: 1em;">&nbsp;</span>
            <input type="text"
                   style="width: 8em;"
                   name="payment"
                   size="8"
                   value="" />
            &euro;
            <button class="pure-button" data-destroy="{parent}">Remove</button>
          </div>
        </fieldset>
        <fieldset>
          <button type="button"
                  class="pure-button"
                  data-clone=".proto-sub-new:not([data-cloned])">
            Add
          </button>
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