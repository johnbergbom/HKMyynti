<%@page pageEncoding="UTF-8"
%><%@include file="include.jsp"
%><% response.setCharacterEncoding("UTF-8"); request.setCharacterEncoding("UTF-8");
%><?xml version="1.0"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
       <title><%--spring:message code="title"/ --%>${sellerName}</title>
       <meta name="keywords" content="hkmyynti, hk-myynti, gardenia, webshop">
       <meta name="description" content="hkmyynti, hk-myynti, gardenia, webshop">
       <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/${sellerName}/seller.css" media="screen" />
    </head>