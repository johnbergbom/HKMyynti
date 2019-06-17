<%@include file="include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <li><a href="addressChange" class="Chosen"><spring:message code="addressChange"/></a></li>
 <li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li>
 <li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li>
 <li><a href="orderStatus" class="NotChosen"><spring:message code="orderStatus"/></a></li>
 <li><a href="operator/login" class="NotChosen"><spring:message code="login.title"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="addressChange"/></h2>
<p><spring:message code="addressChange.help1"/></p>
<p><spring:message code="addressChange.help2"/></p>

<form action="${sellerContextPath}/" method="get">
    <div class="form-item">
	    <input type="submit" value="<spring:message code="cancel"/>"/>
	</div>
</form>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/addressChange">
	<c:import url="showGlobalErrors.jsp"/>
    <div class="form-item">
       <div class="form-label"><spring:message code="addressChange.referenceNumber"/>:</div>
        <form:input path="referenceNumber" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="referenceNumber"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.firstName"/>:</div>
        <form:input path="firstName" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="firstName"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.lastName"/>:</div>
        <form:input path="lastName" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="lastName"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.address"/>:</div>
        <form:input path="address" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="address"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.postCode"/>:</div>
        <form:input path="postCode" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="postCode"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.city"/>:</div>
        <form:input path="city" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="city"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.emailAddress"/>:</div>
        <form:input path="emailAddress" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="emailAddress"/></div>
    </div>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="submit"/>" />
    </div>
</form:form>


 </div>
 