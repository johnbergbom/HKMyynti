<%@include file="include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li>
 <li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li>
 <li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li>
 <li><a href="orderStatus" class="NotChosen"><spring:message code="orderStatus"/></a></li>
 <li><a href="operator/login" class="NotChosen"><spring:message code="login.title"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="refund"/></h2>
<p><spring:message code="refund.help1"/></p>
<p><spring:message code="refund.help2"/></p>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/refund">
	<c:import url="showGlobalErrors.jsp"/>
    <div class="form-item">
       <div class="form-label"><spring:message code="cancelationConfirmation.confirmationCode"/>:</div>
        <form:input path="confirmationCode" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="confirmationCode"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="cancelationConfirmation.accountNumber"/>:</div>
        <form:input path="accountNumber" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="accountNumber"/></div>
    </div>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="submit"/>" />
    </div>
</form:form>


 </div>
 