<%@include file="../include.jsp"
%>

<div id="center-top">
 <ul>
 <li><a href="assignCategories" class="NotChosen"><spring:message code="assignCategories"/></a></li>
 <li><a href="sellStar2MarketCategories" class="NotChosen"><spring:message code="sellStar2MarketCategories"/></a></li>
 <li><a href="translateProducts" class="Chosen"><spring:message code="translateProducts"/></a></li>
 <li><a href="logout" class="NotChosen"><spring:message code="logout"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="totalNumberOfProductsThatCanBeTranslated" arguments="${nbrProducts}"/></h2>

<form action="main" method="get">
    <div class="form-item">
	    <input type="submit" value="<spring:message code="cancel"/>"/>
	</div>
</form>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.6.2.min.js"></script>

<SCRIPT TYPE="text/javascript">
<!--
function updateFirstDetailRow()
{
	url = $('#actionForm').attr('action');
	productId = $('#actionForm').find('input[name="productId"]').val();
	sellerId = $('#actionForm').find('input[name="sellerId"]').val();
	headline = $('#actionForm').find('input[name="headline"]').val();
	$('#detailsDiv').find(':input').addClass('loading');
	$('#details0').load(url, {ajax: "true", productId: productId, headline: headline, sellerId: sellerId}, function(data) {
 		$('#details0').val(data);
 		$('#detailsDiv').find(':input').removeClass('loading');
	});
}
//-->
</SCRIPT>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form id="actionForm" commandName="commandObj" action="${sellerContextPath}/operator/translateProducts">
	<c:import url="../showGlobalErrors.jsp"/>
	<input name="sellerId" type="hidden" value="${sellerId}"/>
	<input name="nbrProducts" type="hidden" value="${nbrProducts}"/>
	<input name="adTemplateLanguage" type="hidden" value="${adTemplateLanguage}"/>
	<input name="productId" type="hidden" value="${productId}"/>
    <div class="form-item">
    	<!-- Display the images. -->
	    <c:forEach items="${imageUrlList}" var="imageUrl">
    	   <img alt="${headline}" src="${imageUrl}" width="200" />
    	</c:forEach>

    	<!-- Headline -->
        <h4><spring:message code="headlinePart"/></h4>
        <div class="form-label">${headlineOrig}</div>
        <%-- <div class="form-label">(${headlineExplanation})</div> --%>
        <%-- <form:input path="headline" size="70" cssErrorClass="form-error-field" onchange="updateFirstDetailRow()" /> --%>
        <form:input path="headline" size="70" cssErrorClass="form-error-field" />
        <div class="form-error-message"><form:errors path="headline"/></div>

			<c:if test="${fn:length(commandObj.details) > 0}">
			    <div id="detailsDiv">
					<!-- Details -->
			        <h4><spring:message code="detailsPart"/></h4>
					<c:forEach begin="0" end="${fn:length(commandObj.details) - 1}" varStatus="loop">
			        	<div class="form-label">${detailsOrig[loop.index]}</div>
					    <%-- <div class="form-label">${detailsExplanation[loop.index]}</div> --%>
					    <form:input path="details[${loop.index}]" size="70" cssErrorClass="form-error-field"/>
					</c:forEach>
			        <div class="form-error-message"><form:errors path="details"/></div>
	    		</div>
		    </c:if>
		
			<c:if test="${fn:length(commandObj.technicalSpecs) > 0}">
				<!-- Technical specs -->
		        <h4><spring:message code="techSpecsPart"/></h4>
				<c:choose>
				    <c:when test='${translationMode == "ALL"}'>
			    		<!-- Make it possible to change the technical specs. -->
						<c:forEach begin="0" end="${fn:length(commandObj.technicalSpecs) - 1}" varStatus="loop">
		        			<div class="form-label">${techSpecsOrig[loop.index]}</div>
				        	<%-- <div class="form-label">${techSpecsExplanation[loop.index]}</div> --%>
				        	<form:input path="technicalSpecs[${loop.index}]" size="70" cssErrorClass="form-error-field"/>
						</c:forEach>
				    </c:when>
				    <c:otherwise>
				    	<!-- translationMode == "HEADLINE_AND_DETAILS", show the technical details as read only -->
		    			<%-- <form:textarea path="technicalSpecs" readonly="true" cssClass="gray" cssErrorClass="form-error-field" rows="${techSpecsRows}" cols="80"/> --%>
						<c:forEach begin="0" end="${fn:length(commandObj.technicalSpecs) - 1}" varStatus="loop">
				        	<form:input path="technicalSpecs[${loop.index}]" size="70" readonly="true" cssClass="gray" cssErrorClass="form-error-field"/>
						</c:forEach>
				    </c:otherwise>
				</c:choose>
		        <div class="form-error-message"><form:errors path="technicalSpecs"/></div>
		    </c:if>
			    
			    <div class="form-item">
			    	<input type="submit" value="<spring:message code="ok"/>" />
		    	</div>
	        

    </div>
</form:form>

</div>

