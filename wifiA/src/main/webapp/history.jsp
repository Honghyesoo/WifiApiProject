<%@ page import="wifiapidb.Wifiservice"%>
<%@ page import="wifiapidb.HistoryDto"%>
<%@ page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
     rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
	<title>경도 위도 히스토리</title>
	<style>
        table {
            border-collapse: collapse;
            width: 100%;
            text-align:center;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: center;
        }
        th {
            background-color: #778899;
            color : white;
        }
       
    </style>
    
    <script>
	    function deleteHistory(id) {
	        if (confirm("정말 삭제하시겠습니까?")) {
	            var xhr = new XMLHttpRequest();
	            xhr.open("POST", "delect.jsp", true);
	            xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	            xhr.onreadystatechange = function () {
	                if (xhr.readyState == 4 && xhr.status == 200) {
	                    var row = document.getElementById("row-" + id);
	                    row.parentNode.removeChild(row);
	                }
	            };
	            xhr.send("id=" + id);
	        }
	    }
	</script>
    
</head>
<body>
   <h1>위치 히스토리 목록</h1>
   <br/>
	<div>
	  <input type="checkbox" class="btn-check" id="btncheck1" autocomplete="off" onclick="location.href='list.jsp'">
	  <label class="btn btn-outline-primary" for="btncheck1">홈</label>
	
	  <input type="checkbox" class="btn-check" id="btncheck2" autocomplete="off" onclick="location.href='history.jsp'">
	  <label class="btn btn-outline-primary" for="btncheck2">위치 히스토리 목록</label>
	
	  <input type="checkbox" class="btn-check" id="btncheck3" autocomplete="off" onclick="location.href='getWifiData.jsp'">
	  <label class="btn btn-outline-primary" for="btncheck3">Open API 와이파이 정보 가져오기</label>
	</div>
	<%
		Wifiservice service = new Wifiservice();
		List<HistoryDto> historyList = service.getHistory();
	%>
	<table>
		<thead>
        	<tr>
        		<th>ID</th>
            	<th>X좌표</th>
           		<th>Y좌표</th>
            	<th>조회일자</th>
            	<th>비고</th>
       		</tr>
       	</thead>
       	<tbody>
       		<%
       		for(HistoryDto list : historyList){
       		%>
       		<tr id="row-<%=list.getId() %>">
       			<td><%=list.getId() %></td>
       			<td><%=list.getLat() %></td>
       			<td><%=list.getLnt() %></td>
       			<td><%=list.getDate() %></td>
       			<td><button onclick="deleteHistory(<%=list.getId()%>)">삭제</button></td>
       		</tr>
       		<%
       			}
       		%>
       	</tbody>
    </table>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" 
    integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
