package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.CreationTimestamp;


@XmlRootElement
@Entity
@Table(name = "tbl_upload")
public class UploadJsonObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	private int jobId;
	
	private Provider provider;
	
	private String requestType;
	
	private String fileName;
	
	private String userName;
	
	@CreationTimestamp
	private Date uploadTimeStamp;
	
	private int numPositionsRequested;
	
	private int numPositionsReturned;
	
	public UploadJsonObject() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getUploadTimeStamp() {
		return uploadTimeStamp;
	}

	public void setUploadTimeStamp(Date uploadTimeStamp) {
		this.uploadTimeStamp = uploadTimeStamp;
	}

	public int getNumPositionsRequested() {
		return numPositionsRequested;
	}

	public void setNumPositionsRequested(int numPositionsRequested) {
		this.numPositionsRequested = numPositionsRequested;
	}

	public int getNumPositionsReturned() {
		return numPositionsReturned;
	}

	public void setNumPositionsReturned(int numPositionsReturned) {
		this.numPositionsReturned = numPositionsReturned;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

}
