package wifiapidb;

import java.sql.Date;

public class HistoryDto {
	private Integer id;
	private Double lat;
	private Double lnt;
	private Date date;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLnt() {
		return lnt;
	}
	public void setLnt(Double lnt) {
		this.lnt = lnt;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
}
