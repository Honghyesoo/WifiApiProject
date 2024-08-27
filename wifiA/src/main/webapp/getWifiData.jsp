<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="wifiapidb.Wifiservice" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>와이파이 정보 가져오기</title>
	<style>
		div{
			text-align: center;
			width: 80%;
			margin: 0 auto;
		}
	</style>
</head>
<body>

<%
Wifiservice service = new Wifiservice();
int count = service.getWifiList();
%>
	<div>
		<h1><%= count %>개의 WIFI 정보를 정상적으로 저장하였습니다.</h1>
		<a href="list.jsp">홈으로 가기</a>
	</div>
</body>
</html>
