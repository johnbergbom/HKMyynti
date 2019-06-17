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
 <h2><spring:message code="frontPage"/></h2>
<table width="830" border="0">
<tbody>
<tr>
<td style="width: 430px;" colspan="2" valign="top" align="left">
<p><spring:message code="presentation"/></p>
</td>
<td style="width: 400px;" colspan="2" align="left" valign="top"><h2 class="mainInfo"><spring:message code="${information}"/></h2></td>
</tr>
<tr>
<td></td>
</tr>
</tbody>
</table>
 </div>
 
 