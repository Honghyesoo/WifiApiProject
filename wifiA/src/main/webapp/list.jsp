<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="wifiapidb.Wifiservice" %>
<%@ page import="wifiapidb.WifiDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
     rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <title>공공 와이파이 정보구하기</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
            text-align:center;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #778899;
            color : white;
        }
         .message {
            font-weight: bold;
            text-align: center;
        }
    </style>
    
    <!-- 위도, 경도 자바스크립트 --> 
    <script>
        function getLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(showPosition);
            } else {
                alert("Geolocation is not supported by this browser.");
            }
        }

        function showPosition(position) {
            document.getElementById("lat").value = position.coords.latitude;
            document.getElementById("lnt").value = position.coords.longitude;
        }
    </script>
    
</head>
<body>
    <h1>와이파이 정보 구하기</h1>
    <br/>
<div>
  <input type="checkbox" class="btn-check" id="btncheck1" autocomplete="off" onclick="location.href='list.jsp'">
  <label class="btn btn-outline-primary" for="btncheck1">홈</label>

  <input type="checkbox" class="btn-check" id="btncheck2" autocomplete="off" onclick="location.href='history.jsp'">
  <label class="btn btn-outline-primary" for="btncheck2">위치 히스토리 목록</label>

  <input type="checkbox" class="btn-check" id="btncheck3" autocomplete="off" onclick="location.href='getWifiData.jsp'">
  <label class="btn btn-outline-primary" for="btncheck3">Open API 와이파이 정보 가져오기</label>
</div>

  
    <br/>
    <div>
        <form id="locationForm" method="POST" action="list.jsp">
            <label for="lat">LAT :</label>
            <input type="text" id="lat" name="lat" placeholder="X좌표"readonly>
           
            <label for="lnt">LNT :</label>
            <input type="text" id="lnt" name="lnt" placeholder="Y좌표" readonly>
         	<button type="button" class="btn btn-outline-primary" onclick="getLocation()">내 위치 가져오기</button>
         	<button type="submit" class="btn btn-outline-primary">근처 wifi 정보 보기</button>
            
        </form>
    </div>
 
    <table>
        <tr>
            <th>거리(km)</th> 
            <th>관리번호</th>
            <th>자치구</th>
            <th>와이파이명</th>
            <th>도로명 주소</th>
            <th>상세 주소</th>
            <th>설치 위치(층)</th>
            <th>설치 기관</th>
            <th>설치 유형</th>
            <th>서비스 구분</th>
            <th>망 종류</th>
            <th>설치 년도</th>
            <th>실내 외 구분</th>
            <th>WIFI 접속 환경</th>
            <th>x좌표</th>
            <th>y좌표</th>
            <th>작업일자</th>
        </tr>
         <%
            String latStr = request.getParameter("lat");
            String lntStr = request.getParameter("lnt");

            if (latStr == null || lntStr == null || latStr.isEmpty() || lntStr.isEmpty()) {
        %>
        <tr>
            <td colspan="17" class="message">위치 정보를 입력한 후에 조회해 주세요</td>
        </tr>
        <%
            } else {
                double lat = Double.parseDouble(latStr);
                double lnt = Double.parseDouble(lntStr);

                Wifiservice service = new Wifiservice();
                service.postHistory(lat, lnt);
                List<WifiDto> wifiList = service.getNearestWifi(lat, lnt);
                
                for (WifiDto wifi : wifiList) {
        %>
        <tr>
            <td><%= wifi.getDistance() %></td>
            <td><%= wifi.getxSwifiMgrNo() %></td>
            <td><%= wifi.getxSwifiWrdofc() %></td>
            <td><%= wifi.getxSwifiMainNm() %></td>
            <td><%= wifi.getxSwifiAdres1() %></td>
            <td><%= wifi.getxSwifiAdres2() %></td>
            <td><%= wifi.getxSwifiInstlFloor() %></td>
            <td><%= wifi.getxSwifiInstlTy() %></td>
            <td><%=wifi.getxSwifiInstlMby() %>
            <td><%= wifi.getxSwifiSvcSe() %></td>
            <td><%= wifi.getxSwifiCmcwr() %></td>
            <td><%= wifi.getxSwifiCnstcYear() %></td>
            <td><%= wifi.getxSwifiInoutDoor() %></td>
            <td><%= wifi.getxSwifiRemars3() %></td>
            <td><%= wifi.getLat() %></td>
            <td><%= wifi.getLnt() %></td>
            <td><%= wifi.getWorkDttm() %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" 
    integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>