package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "tbl_client")
public class VPClient {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column
	private String client;
	
	@Column
	private String country;
	
	@Column
	private String engagementCode;
	
	@Column
	private String paceNo;
	
	@Column
	private Date startPace;
	
	@Column
	private Date endPace;
	
	@Column
	private String duns;
	
	@Column
	private String comment;
	
	public VPClient() {
		// TODO Auto-generated constructor stub
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getEngagementCode() {
		return engagementCode;
	}
	public void setEngagementCode(String engagementCode) {
		this.engagementCode = engagementCode;
	}
	public String getPaceNo() {
		return paceNo;
	}
	public void setPaceNo(String paceNo) {
		this.paceNo = paceNo;
	}
	public Date getStartPace() {
		return startPace;
	}
	public void setStartPace(Date startPace) {
		this.startPace = startPace;
	}
	public Date getEndPace() {
		return endPace;
	}
	public void setEndPace(Date endPace) {
		this.endPace = endPace;
	}
	public String getDuns() {
		return duns;
	}
	public void setDuns(String duns) {
		this.duns = duns;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
