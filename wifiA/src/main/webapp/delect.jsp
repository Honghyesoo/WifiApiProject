<%@ page import="wifiapidb.Wifiservice"%>
<%@ page import="wifiapidb.HistoryDto"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    Integer id = Integer.parseInt(request.getParameter("id"));
    Wifiservice wifiservice = new Wifiservice();
    
    wifiservice.delectHistory(id);
    
    response.setContentType("text/plain");
    response.getWriter().write("success");
%>
