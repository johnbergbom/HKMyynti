<%@include file="include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li>
 <li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li>
 <li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li>
 <li><a href="orderStatus" class="Chosen"><spring:message code="orderStatus"/></a></li>
 <li><a href="operator/login" class="NotChosen"><spring:message code="login.title"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="orderStatus"/></h2>
<p><spring:message code="orderStatus.help1"/></p>
<p><spring:message code="orderStatus.help2"/></p>

<form action="${sellerContextPath}/" method="get">
    <div class="form-item">
	    <input type="submit" value="<spring:message code="cancel"/>"/>
	</div>
</form>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/orderStatus">
	<c:import url="showGlobalErrors.jsp"/>
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
 