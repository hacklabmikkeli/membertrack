<%@ tag description="Edit personal info about membership" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="e" uri="/WEB-INF/extra-tags" %>
<%@ attribute name="membership" type="fi.ilmoeuro.membertrack.entity.Entity" %>
<%@ attribute name="gotoUrl" type="java.lang.String" %>
<%@ attribute name="personsUrl" type="java.lang.String" %>
<c:set var="personId" value="${membership.id}" />
<div class="popup hide-nontarget" id="edit-person-${personId}">
    <form
        method="POST"
        action="${personsUrl}update"
        class="pure-form pure-form-aligned">
        <input type="hidden" name="personId" value="${personId}" />
        <input type="hidden" name="goto" value="${gotoUrl}" />
        <fieldset>
            <legend>Personal information</legend>
            <div class="pure-control-group">
                <label for="lb-${e:nextNum()}">Full name</label>
                <input id="lb-${e:lastNum()}" 
                       type="text"
                       name="fullName"
                       value="${membership.value.person.fullName}" />
            </div>
            <div class="pure-control-group">
                <label for="lb-${e:nextNum()}">Email</label>
                <input id="lb-${e:lastNum()}"
                       type="text"
                       name="email"
                       value="${membership.value.person.email}" />
            </div>
        </fieldset>
        <fieldset>
            <legend>Phone numbers</legend>
            <c:forEach items="${membership.value.person.phoneNumbers}" var="phoneNumber">
            <div class="pure-control-group">
                <label></label>
                <input type="text"
                       name="phoneNumber"
                       value="${phoneNumber.phoneNumber}" />
                <button type="button"
                        class="pure-button"
                        data-destroy="{parent}">
                    Remove
                </button>
            </div>
            </c:forEach>
            <div class="pure-control-group hide-prototype prototype-new-pn">
                <label></label>
                <input type="text"
                       name="phoneNumber"
                       />
                <button type="button"
                        class="pure-button"
                        data-destroy="{parent}">
                    Remove
                </button>
            </div>
        </fieldset>
        <fieldset>
            <button type="button"
                    class="pure-button"
                    data-clone=".prototype-new-pn:not([data-cloned])">
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