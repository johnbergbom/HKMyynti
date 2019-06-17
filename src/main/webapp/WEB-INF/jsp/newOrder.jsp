<%@include file="include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="Chosen"><spring:message code="placeNewOrder"/></a></li -->
 <li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li>
 <li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li>
 <li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li>
 <li><a href="orderStatus" class="NotChosen"><spring:message code="orderStatus"/></a></li>
 <li><a href="operator/login" class="NotChosen"><spring:message code="login.title"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="placeNewOrder"/></h2>
<p><spring:message code="newOrder.helpWithMarketSalesId"/></p>
<p><spring:message code="newOrder.helpWithPrice"/></p>
<spring:message code="newOrder.helpWithBundlingTitle"/><br/>
<spring:message code="newOrder.helpWithBundlingConditions"/><br/>
1.) <spring:message code="newOrder.helpWithBundlingConditionOne"/></br><br/>
2.) <spring:message code="newOrder.helpWithBundlingConditionTwo"/></br><br/>
</br></br>
<spring:message code="newOrder.helpWithBundlingNoteOne"/><br/><br/>
<spring:message code="newOrder.helpWithBundlingNoteTwo"/><br/><br/>
<spring:message code="newOrder.helpWithBundlingEnd"/>

<br/><br/>
Lue meidän toimitusehdot <a href="deliveryInfo" target="_blank">tässä</a>.
<br/><br/>

<form action="${sellerContextPath}/" method="get">
    <div class="form-item">
	    <input type="submit" value="<spring:message code="cancel"/>"/>
	</div>
</form>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/newOrder">
	<c:import url="showGlobalErrors.jsp"/>
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
       <div class="form-label"><spring:message code="newOrder.marketSalesId"/>:</div>
        <form:input path="marketSalesId" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="marketSalesId"/></div>
    </div>
    <div class="form-item">
       <div class="form-label"><spring:message code="newOrder.amount"/>:</div>
        <form:input path="amount" size="40" cssErrorClass="form-error-field"/>
        <div class="form-error-message"><form:errors path="amount"/></div>
    </div>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="submit"/>" />
    </div>
</form:form>


 </div>
 
 