<%@include file="../include.jsp"
%><c:import url="../header.jsp"/><body>

 <div id="main">

 <div id="header">
 <div id="header-center">
 <p>&nbsp;</p>
 <h1><a href="${sellerUrl}${sellerContextPath}/"><%--spring:message code="${sellerName}"/ --%>
 <img src="http://sellstar.fi/images/seller/${sellerName}/logo.jpg" alt="${sellerName}" /></a></h1>
 </div>

 <div id="header-right">
 </div> 
 
 </div> <!-- end of header div -->

 

 <div id="content">

 <div id="left">
 <div id="left-top">
 </div>
 <div id="left-mid">
 </div>
 <div id="left-bot">
 </div>
 </div><!-- end of left div -->


 <div id="center">





 

 <c:import url="${whichPage}.jsp"/>
 
 <div id="center-bot">
 </div>

 </div><!-- end of center div -->

 <div id="right">
 <div id="right-top">
 </div>
 <div id="right-mid">
 </div>
 <div id="right-bot">
 </div>
 </div><!-- end of right div -->

 </div><!-- end of content div -->

 <div id="footer">
 <div id="footer-left">
 </div>

 <div id="footer-center">
 <p>&nbsp;</p>
<p>&nbsp;</p>
<p><a href="${sellerUrl}${sellerContextPath}/">${sellerName}</a></p>
 </div>

 
 <div id="footer-right">
 </div>
 </div><!-- end of footer div -->

<div id="laskuri">
</div>



</div><!-- end of main div -->

</body></html>
