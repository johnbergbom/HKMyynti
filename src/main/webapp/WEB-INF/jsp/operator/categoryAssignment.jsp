<%@include file="../include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <!-- li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li -->
 <!-- li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li -->
 <!-- li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li -->
 <li><a href="assignCategories" class="Chosen"><spring:message code="assignCategories"/></a></li>
 <li><a href="sellStar2MarketCategories" class="NotChosen"><spring:message code="sellStar2MarketCategories"/></a></li>
 <li><a href="translateProducts" class="NotChosen"><spring:message code="translateProducts"/></a></li>
 <li><a href="logout" class="NotChosen"><spring:message code="logout"/></a></li>
 </ul>
 </div>

<div id="center-mid">
 <h2><spring:message code="totalNumberOfProductsThatCanBeAssignedCategories" arguments="${nbrProducts}"/></h2>

<form action="main" method="get">
    <div class="form-item">
	    <input type="submit" value="<spring:message code="cancel"/>"/>
	</div>
</form>

<SCRIPT TYPE="text/javascript">
<!--
function popup(mylink, windowname)
{
if (! window.focus)return true;
var href;
if (typeof(mylink) == 'string')
   href=mylink;
else
   href=mylink.href;
window.open(href, windowname, 'width=700,height=600,scrollbars=yes');
return false;
}
//-->
</SCRIPT>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/operator/assignCategories">
	<c:import url="../showGlobalErrors.jsp"/>
	<input name="adTemplateId" type="hidden" value="${adTemplateId}"/>
	<input name="number" type="hidden" value="${number}"/>
	<input name="viewType" type="hidden" value="${viewType}"/>
    <!-- div class="form-item">
       <img alt="${adTemplateHeadline}" src="${imageUrl}" width="200" />
    </div -->
    <div class="form-item">
       <h3><!-- spring:message code="productHeadline"/ -->${headline}</h3>
       <!-- br/ -->
       ${headlineOrig}
       <br>
       (adTemplateId = ${adTemplateId}, productId = ${productId})
    </div>
    <c:forEach items="${imageUrlList}" var="imageUrl">
       <img alt="${headline}" src="${imageUrl}" width="200" />
    </c:forEach>
    <!-- div class="form-item">
       <spring:message code="providerName"/>: ${headlineOrig}
    </div -->
    <div class="form-item">
       <h4><spring:message code="technicalSpecs"/>:</h4>
       <c:forEach items="${technicalSpecs}" var="specs">
       		${specs}<br/>
       </c:forEach>
       <c:forEach items="${technicalSpecsOrig}" var="specsOrig">
       		${specsOrig}<br/>
       </c:forEach>
    </div>
    <div class="form-item">
       <h4><spring:message code="details"/>:</h4>
       ${details}
       <br/>
       ${detailsOrig}
    </div>
    <br/>
    <br/>
    <div class="form-item">
       <spring:message code="providerCategory"/>: ${providerCategory}
    </div>
    <div class="form-item">
       <spring:message code="providerUrl"/>: <a href="${providerProductUrl}" target="_blank">${providerProductUrl}</a>
    </div>
    <br/>

	<!-- Print the category options. -->
	<!-- The following row doesn't work, because it puts all options on the same row, so let's -->
	<!-- instead iterate over the single items in the list. -->
    <!-- form:radiobuttons items="${categories}" path="categoryId" itemLabel="label"/ -->
    <h3><spring:message code="chooseOneOfTheFollowingCategories"/> (<spring:message code="${hardness}"/>)</h3>
    <table>
	    <c:forEach items="${categories}" var="category">
	    	<tr><td><form:radiobutton path="categoryId" value="${category.category}" cssClass="${category.cssClass}" label="${category.label}"/><a href="listProductsInCategory?categoryId=${category.category}" onClick="return popup(this, 'adTemplateForProduct${category.category}')"><spring:message code="productList"/></a></td></tr>
    	</c:forEach>
    </table>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="ok"/>" />
    </div>
</form:form>

</div>
 