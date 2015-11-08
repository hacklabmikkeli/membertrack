<%@ tag description="Edit personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.member.Membership" %>
<%@ attribute name="gotoUrl" type="java.lang.String" %>
<%@ attribute name="personsUrl" type="java.lang.String" %>
<c:set var="personId" value="${membership.person.id}" />
<div class="popup" id="person-${personId}-edit">
    <form
        method="POST"
        action="${personsUrl}update"
        class="pure-form pure-form-aligned">
        <input type="hidden" name="personId" value="${personId}" />
        <input type="hidden" name="goto" value="${gotoUrl}" />
        <fieldset>
            <legend>Personal information</legend>
            <div class="pure-control-group">
                <label for="person-${personId}-edit-email">Full name</label>
                <input id="person-${personId}-edit-email"
                       type="text"
                       name="fullName"
                       value="${membership.person.value.fullName}" />
            </div>
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
            <div class="pure-control-group">
                <label for="person-${personId}-edit-pn-new}"></label>
                <input id="person-${personId}-edit-pn-new"
                       type="text"
                       name="phoneNumber"
                       />
            </div>
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