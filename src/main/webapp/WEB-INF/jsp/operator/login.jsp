<%@include file="../include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <!-- li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li -->
 <!-- li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li -->
 <!-- li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li -->
 <li><a href="assignCategories" class="NotChosen"><spring:message code="assignCategories"/></a></li>
 <li><a href="sellStar2MarketCategories" class="NotChosen"><spring:message code="sellStar2MarketCategories"/></a></li>
 <li><a href="translateProducts" class="NotChosen"><spring:message code="translateProducts"/></a></li>
 <!-- li><a href="logout" class="NotChosen"><spring:message code="logout"/></a></li -->
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="login.title"/></h2>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/operator/login">
	<c:import url="../showGlobalErrors.jsp"/>
	<form:hidden path="redirect"/>
    <div class="form-item">
       <div class="form-label"><spring:message code="login.name"/>:</div>
        <form:input path="userName" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="userName"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="login.password"/>:</div>
        <form:password path="password" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="password"/></div>
    </div>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="submit"/>" />
    </div>
</form:form>


 </div>
 