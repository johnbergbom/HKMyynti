<%@include file="../include.jsp"
%>
<html>
  <body>
    <h4>${path}</h4>
    <table>
	    <c:forEach items="${adTemplateInfoList}" var="adTemplateInfo">
	    	<tr><td>${adTemplateInfo.headline} (${adTemplateInfo.id})</td></tr>
    	</c:forEach>
    </table>
  </body>
</html>