<%@include file="../include.jsp"
%>

<div id="center-top">
 <ul>
 <!-- li><a href="newOrder" class="NotChosen"><spring:message code="placeNewOrder"/></a></li -->
 <!-- li><a href="addressChange" class="NotChosen"><spring:message code="addressChange"/></a></li -->
 <!-- li><a href="orderCancelation" class="NotChosen"><spring:message code="cancelOrder"/></a></li -->
 <!-- li><a href="commonBill" class="NotChosen"><spring:message code="commonBill"/></a></li -->
 <li><a href="assignCategories" class="NotChosen"><spring:message code="assignCategories"/></a></li>
 <li><a href="sellStar2MarketCategories" class="Chosen"><spring:message code="sellStar2MarketCategories"/></a></li>
 <li><a href="translateProducts" class="NotChosen"><spring:message code="translateProducts"/></a></li>
 <li><a href="logout" class="NotChosen"><spring:message code="logout"/></a></li>
 </ul>
 </div>

<div id="center-mid">

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

<h2><spring:message code="followingSellStarCategoryMissingMapping"/>: ${sellStarCategoryPath}</h2>

<%-- Give command object a meaningful name instead of using default name, 'command' --%>
<form:form commandName="commandObj" action="${sellerContextPath}/operator/sellStar2MarketCategories">
	<c:import url="../showGlobalErrors.jsp"/>
	<input name="sellStarCategoryId" type="hidden" value="${sellStarCategoryId}"/>
	<input name="marketName" type="hidden" value="${marketName}"/>
	<!-- Print the category options. -->
    <h3><spring:message code="chooseSomeOfTheFollowingCategories"/></h3>
    <table>
	    <c:forEach items="${categories}" var="category">
	    	<tr><td><form:checkbox path="categoryIds" value="${category.category}" label="${category.label}"/><!-- a href="listProductsInCategory?categoryId=${category.category}" onClick="return popup(this, 'adTemplateForProduct${category.category}')"><spring:message code="productList"/></a --></td></tr>
    	</c:forEach>
    </table>
    <div class="form-item">
    	<input type="submit" value="<spring:message code="ok"/>" />
    </div>
</form:form>

</div>
