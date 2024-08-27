package wifiapidb;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Wifiservice {

    private String url = "jdbc:mariadb://localhost/wifi?useUnicode=true&characterEncoding=UTF-8";
    private String dbUserId = "api";
    private String dbpassword = "1245";
    private static final String API_URL = "http://openapi.seoul.go.kr:8088/4e6271726a676b733735454e517768/json/TbPublicWifiInfo/";

    // 데이터베이스 연결 메서드
    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(url, dbUserId, dbpassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // 자원 해제 메서드
    private void closeResources(ResultSet rs, PreparedStatement preparedStatement, Connection connection) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getWifiList() {
        int totalCount = 0;
        int pageSize = 1000;
        int startIndex = 1;
        int count = 0;

        try {
            // 전체 데이터 수를 가져옴
            String totalCountUrl = "http://openapi.seoul.go.kr:8088/4e6271726a676b733735454e517768/json/TbPublicWifiInfo/1/1/";
            String jsonResponse = getJsonResponse(totalCountUrl);
            totalCount = extractTotalCount(jsonResponse);

            System.out.println("Total Count: " + totalCount);

            while (startIndex <= totalCount) {
                String apiUrl = String.format("http://openapi.seoul.go.kr:8088/4e6271726a676b733735454e517768/json/TbPublicWifiInfo/%d/%d/", startIndex, startIndex + pageSize - 1);
                jsonResponse = getJsonResponse(apiUrl);

                List<WifiDto> wifiList = parseWifiData(jsonResponse);
                for (WifiDto wifiDto : wifiList) {
                    saveWifiToDB(wifiDto);
                    count++;
                }

                startIndex += pageSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    private String getJsonResponse(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    private int extractTotalCount(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"list_total_count\":") + 20;
        int endIndex = jsonResponse.indexOf(",", startIndex);
        return Integer.parseInt(jsonResponse.substring(startIndex, endIndex));
    }

    private List<WifiDto> parseWifiData(String jsonResponse) {
        List<WifiDto> wifiList = new ArrayList<>();
        int startIndex = jsonResponse.indexOf("\"row\":[") + 6;
        int endIndex = jsonResponse.lastIndexOf("]");
        String rowData = jsonResponse.substring(startIndex, endIndex);

        String[] wifiDataArray = rowData.split("\\},\\{");

        for (String wifiData : wifiDataArray) {
            WifiDto wifiDto = new WifiDto();
            wifiDto.setxSwifiMgrNo(extractValue(wifiData, "X_SWIFI_MGR_NO"));
            wifiDto.setxSwifiWrdofc(extractValue(wifiData, "X_SWIFI_WRDOFC"));
            wifiDto.setxSwifiMainNm(extractValue(wifiData, "X_SWIFI_MAIN_NM"));
            wifiDto.setxSwifiAdres1(extractValue(wifiData, "X_SWIFI_ADRES1"));
            wifiDto.setxSwifiAdres2(extractValue(wifiData, "X_SWIFI_ADRES2"));
            wifiDto.setxSwifiInstlFloor(extractValue(wifiData, "X_SWIFI_INSTL_FLOOR"));
            wifiDto.setxSwifiInstlTy(extractValue(wifiData, "X_SWIFI_INSTL_TY"));
            wifiDto.setxSwifiInstlMby(extractValue(wifiData, "X_SWIFI_INSTL_MBY"));
            wifiDto.setxSwifiSvcSe(extractValue(wifiData, "X_SWIFI_SVC_SE"));
            wifiDto.setxSwifiCmcwr(extractValue(wifiData, "X_SWIFI_CMCWR"));
            wifiDto.setxSwifiCnstcYear(extractValue(wifiData, "X_SWIFI_CNSTC_YEAR"));
            wifiDto.setxSwifiInoutDoor(extractValue(wifiData, "X_SWIFI_INOUT_DOOR"));
            wifiDto.setxSwifiRemars3(extractValue(wifiData, "X_SWIFI_REMARS3"));
            wifiDto.setLat(extractValue(wifiData, "LAT"));
            wifiDto.setLnt(extractValue(wifiData, "LNT"));
            wifiDto.setWorkDttm(extractValue(wifiData, "WORK_DTTM"));
            wifiList.add(wifiDto);
        }
        return wifiList;
    }

    // 와이파이 정보를 DB에 저장하는 메서드
    private void saveWifiToDB(WifiDto wifiDto) {
        String query = "INSERT INTO public_wifi (X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LAT, LNT, WORK_DTTM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, wifiDto.getxSwifiMgrNo());
            preparedStatement.setString(2, wifiDto.getxSwifiWrdofc());
            preparedStatement.setString(3, wifiDto.getxSwifiMainNm());
            preparedStatement.setString(4, wifiDto.getxSwifiAdres1());
            preparedStatement.setString(5, wifiDto.getxSwifiAdres2());
            preparedStatement.setString(6, wifiDto.getxSwifiInstlFloor());
            preparedStatement.setString(7, wifiDto.getxSwifiInstlTy());
            preparedStatement.setString(8, wifiDto.getxSwifiInstlMby());
            preparedStatement.setString(9, wifiDto.getxSwifiSvcSe());
            preparedStatement.setString(10, wifiDto.getxSwifiCmcwr());
            preparedStatement.setString(11, wifiDto.getxSwifiCnstcYear());
            preparedStatement.setString(12, wifiDto.getxSwifiInoutDoor());
            preparedStatement.setString(13, wifiDto.getxSwifiRemars3());
            preparedStatement.setString(14, wifiDto.getLat());
            preparedStatement.setString(15, wifiDto.getLnt());
            preparedStatement.setString(16, wifiDto.getWorkDttm());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, preparedStatement, connection);
        }
    }

    // JSON 데이터에서 특정 키의 값을 추출하는 메서드
    private String extractValue(String data, String key) {
        int startIndex = data.indexOf("\"" + key + "\":\"") + key.length() + 4;
        int endIndex = data.indexOf("\"", startIndex);
        return data.substring(startIndex, endIndex);
    }

    // 사용자의 위치에서 가장 가까운 와이파이 정보를 가져오는 메서드
    public List<WifiDto> getNearestWifi(double lat, double lnt) {
        List<WifiDto> wifiList = new ArrayList<>();
        String query = "SELECT X_SWIFI_MGR_NO, X_SWIFI_WRDOFC, X_SWIFI_MAIN_NM, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INSTL_FLOOR, X_SWIFI_INSTL_TY, X_SWIFI_INSTL_MBY, X_SWIFI_SVC_SE, X_SWIFI_CMCWR, X_SWIFI_CNSTC_YEAR, X_SWIFI_INOUT_DOOR, X_SWIFI_REMARS3, LAT, LNT, WORK_DTTM, " +
                       "(6371 * acos(cos(radians(?)) * cos(radians(LAT)) * cos(radians(LNT) - radians(?)) + sin(radians(?)) * sin(radians(LAT)))) AS distance " +
                       "FROM public_wifi " +
                       "ORDER BY distance ASC " +
                       "LIMIT 20";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, lat);
            preparedStatement.setDouble(2, lnt);
            preparedStatement.setDouble(3, lat);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                WifiDto wifiDto = new WifiDto();
                wifiDto.setxSwifiMgrNo(resultSet.getString("X_SWIFI_MGR_NO"));
                wifiDto.setxSwifiWrdofc(resultSet.getString("X_SWIFI_WRDOFC"));
                wifiDto.setxSwifiMainNm(resultSet.getString("X_SWIFI_MAIN_NM"));
                wifiDto.setxSwifiAdres1(resultSet.getString("X_SWIFI_ADRES1"));
                wifiDto.setxSwifiAdres2(resultSet.getString("X_SWIFI_ADRES2"));
                wifiDto.setxSwifiInstlFloor(resultSet.getString("X_SWIFI_INSTL_FLOOR"));
                wifiDto.setxSwifiInstlTy(resultSet.getString("X_SWIFI_INSTL_TY"));
                wifiDto.setxSwifiInstlMby(resultSet.getString("X_SWIFI_INSTL_MBY"));
                wifiDto.setxSwifiSvcSe(resultSet.getString("X_SWIFI_SVC_SE"));
                wifiDto.setxSwifiCmcwr(resultSet.getString("X_SWIFI_CMCWR"));
                wifiDto.setxSwifiCnstcYear(resultSet.getString("X_SWIFI_CNSTC_YEAR"));
                wifiDto.setxSwifiInoutDoor(resultSet.getString("X_SWIFI_INOUT_DOOR"));
                wifiDto.setxSwifiRemars3(resultSet.getString("X_SWIFI_REMARS3"));
                wifiDto.setLat(resultSet.getString("LAT"));
                wifiDto.setLnt(resultSet.getString("LNT"));
                wifiDto.setWorkDttm(resultSet.getString("WORK_DTTM"));
                // distance 값 포맷팅
                double distanceValue = resultSet.getDouble("distance");
                String formattedDistance = String.format("%.2f km", distanceValue);
                wifiDto.setDistance(formattedDistance);  // 포맷팅된 distance 값 설정
                
                wifiList.add(wifiDto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, preparedStatement, connection);
        }

        return wifiList;
    }

    
    // 위도 경도 히스토리 저장
    public void postHistory(double lat, double lnt) {
    	String query = "INSERT INTO history(lat, lnt, date) VALUE (?,?,now())";
    	
    	   Connection connection = null;
           PreparedStatement preparedStatement = null;
           ResultSet resultSet = null;
           
           try {
               connection = getConnection();
               preparedStatement = connection.prepareStatement(query);
               preparedStatement.setDouble(1, lat);
               preparedStatement.setDouble(2, lnt);
               
               preparedStatement.executeUpdate();
           } catch (SQLException e) {
               e.printStackTrace();
           } finally {
               closeResources(resultSet, preparedStatement, connection);
           }
       }
    
    // 위도 경도 히스토리 목록 보기
    public List<HistoryDto> getHistory(){
    	List<HistoryDto> historyList = new ArrayList<>();
    	
    	Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		
		try {
			connection = getConnection();
    		String query = "SELECT * FROM history ORDER BY id DESC";
    		preparedStatement = connection.prepareStatement(query);
    		rs = preparedStatement.executeQuery();
    		
    		//다음 데이터가 있을때까지 실행한다.
    		while(rs.next()) {
    			Integer id = rs.getInt("id");
    			Double lat = rs.getDouble("lat");
    			Double lnt = rs.getDouble("lnt");
    			Date date = rs.getDate("date");
    			
    			HistoryDto historyDto = new HistoryDto();
    			historyDto.setId(rs.getInt("id"));
    			historyDto.setLat(rs.getDouble("lat"));
    			historyDto.setLnt(rs.getDouble("lnt"));
    			historyDto.setDate(rs.getDate("date"));
    			historyList.add(historyDto);
    		}
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeResources(rs, preparedStatement, connection);
		}
		return historyList;
    }
    //히스토리 목록 삭제
    public void delectHistory(Integer id ) {
    	Connection connection = null;
        PreparedStatement preparedStatement = null;
        
        try {
        	connection = getConnection();
        	
        	//연결이 되었으며 다음 쿼리문 실행한다
        	String sql ="DELETE from history "
        			+ "where id=?;";
        	preparedStatement = connection.prepareStatement(sql);
        	preparedStatement.setInt(1,id);
        	int affected = preparedStatement.executeUpdate();
        	 if (affected > 0){
                 System.out.println("삭제 성공");
             }else {
                 System.out.println("삭제 실패");
             }

         } catch (SQLException e) {
             e.printStackTrace();
         }finally {
             closeResources(null, preparedStatement, connection);
         }
        
    }
}











