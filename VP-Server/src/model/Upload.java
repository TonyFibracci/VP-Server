package model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.Session;
import org.hibernate.annotations.CreationTimestamp;

import com.google.gson.Gson;

@XmlRootElement
@Entity
@Table(name = "tbl_upload")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "provider")
public abstract class Upload {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column
	private int jobId;
	
	@Column
	private String requestType;
	
	@Column
	private String fileName;
	
	@Column
	private String userName;
	
	@Column
	@CreationTimestamp
	private Date uploadTimeStamp;
	
	@Column
	private int numPositionsRequested;
	
	@Column
	private int numPositionsReturned;
	
	@Transient
	private String fileAbsolutePath;
	
	@Transient
	private String replyFileName;
	
	@Transient
	private String folderPath;
	
	@Transient
	private String replyContent;
	
	@Transient
	private String replyPath;
	
	@Transient
	private String sqlCompatibleReplyPath;
	
	@Column
	private boolean error;
	
	@Transient
	private boolean express;
	
	
	public Upload() {
		
	}
	

	public int getUploadID() {
		return id;
	}

	public void setUploadID(int uploadID) {
		this.id = uploadID;
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

	public Date getUploadTimeStamp() {
		return uploadTimeStamp;
	}

	public void setUploadTimeStamp(Date uploadTimeStamp) {
		this.uploadTimeStamp = uploadTimeStamp;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getNumPositionsReturned() {
		return numPositionsReturned;
	}

	public void setNumPositionsReturned(int numPositionsReturned) {
		this.numPositionsReturned = numPositionsReturned;
	}

	
	public int getNumPositionsRequested() {
		return numPositionsRequested;
	}


	public void setNumPositionsRequested(int numPositionsRequested) {
		this.numPositionsRequested = numPositionsRequested;
	}


	public String getFileAbsolutePath() {
		return fileAbsolutePath;
	}

	public void setFileAbsolutePath(String fileAbsolutePath) {
		this.fileAbsolutePath = fileAbsolutePath;
	}

	public String getFolderPath() {
		return folderPath;
	}


	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}



	
	
	public String getReplyContent() {
		return replyContent;
	}


	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}


	public String getReplyPath() {
		return replyPath;
	}


	public void setReplyPath(String replyPath) {
		this.replyPath = replyPath;
	}


	public String getSqlCompatibleReplyPath() {
		return sqlCompatibleReplyPath;
	}


	public void setSqlCompatibleReplyPath(String sqlCompatibleReplyPath) {
		this.sqlCompatibleReplyPath = sqlCompatibleReplyPath;
	}


	public void setReplyFileName(String replyFileName) {
		this.replyFileName = replyFileName;
	}

	
	public boolean isError() {
		return error;
	}


	public void setError(boolean error) {
		this.error = error;
	}


	public boolean isExpress() {
		return express;
	}


	public void setExpress(boolean express) {
		this.express = express;
	}

	
	/**
	 * factory method for upload object creation
	 * @param provider
	 * @return
	 */
	public static Upload createUpload(Provider provider) {
		if(provider == Provider.BLOOMBERG)
			return new BloombergUpload();
		else if(provider == Provider.MARKIT) {
			return new MarkitUpload();
		}
		else if(provider == Provider.ICE) {
			return new ICEUpload();
		}
		else
			return null;
	}
	
//	public void save() {
//		Session session = HibernateUtil.getSessionFactory().openSession();	
//		try {		
//			session.beginTransaction();
//			session.save(this);
//			session.getTransaction().commit();			
//		} finally {
//			session.close();
//		}
//	}
	
	
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();	
		stringBuilder.append("Request Type: ").append(requestType).append("\n");
		stringBuilder.append("File: ").append(fileName).append("\n");
		return stringBuilder.toString();		
	}
	
	public abstract void importMarketDataReply() throws Exception;


}
