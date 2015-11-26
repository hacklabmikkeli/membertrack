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
<t:master sidebar="false">
    <div class="popup">
      <form
          method="POST"
          action="${it.common.paths.authentication}startSession"
          class="pure-form pure-form-aligned">
        <input type="hidden" name="goto" value="${it.common.paths.memberships}" />
        <fieldset>
          <div class="pure-control-group">
            <label for="email">Email</label>
            <input id="email"
                   type="text"
                   name="email" />
          </div>
          <div class="pure-control-group">
            <label for="password">Password</label>
            <input id="password"
                   type="password"
                   name="password" />
          </div>
        </fieldset>
        <fieldset>
          <div class="pure-control-group for-buttons">
            <button type="submit" class="pure-button">
              Log in
            </button>
          </div>
        </fieldset>

      </form>
    </div>
</t:master>