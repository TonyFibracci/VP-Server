package model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@XmlRootElement
@Entity
@Table(name = "tbl_job")
public class Job {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column
	private String customer;
	
	@Column
	private String portfolio;
	
	@Column
	private JobType type;
	
	@Column
	private Date pricingDay;
	
	@Column
	private String currency;
	
	@ManyToOne 
	@JoinColumn(name = "preparer")
	private User preparer;
	
	@ManyToOne 
	@JoinColumn(name = "reviewer")
	private User reviewer;
	
	@Column 
	private Date deadline;
	
	@Column
	private JobStatus status;
	
	@Column
	private String engagementCode;
	
	@Column
	private String contacts;
	
	@Column
	private String comment;
	
	@Column
	private float hours;
	
	public Job() {
		// TODO Auto-generated constructor stub
	}
	
	public Job(JsonObject jsonObj) {
		Gson gson = new Gson();
		if(jsonObj.get("id") != null) {
			this.id = jsonObj.get("id").getAsInt();
		}
		if(jsonObj.get("customer") != null) {
			this.customer = jsonObj.get("customer").getAsString();
		}
		if(jsonObj.get("portfolio") != null) {
			this.portfolio = jsonObj.get("portfolio").getAsString();
		}
		if(jsonObj.get("preparer") != null) {
			this.preparer = gson.fromJson(jsonObj.get("preparer"), User.class);
		}
		if(jsonObj.get("reviewer") != null) {
			this.reviewer = gson.fromJson(jsonObj.get("reviewer"), User.class);
		}
		if(jsonObj.get("currency") != null) {
			this.currency = jsonObj.get("currency").getAsString();
		}
		if(jsonObj.get("type") != null) {
			this.type = JobType.valueOf(jsonObj.get("type").getAsString());
		}
		if(jsonObj.get("status") != null) {
			this.status = JobStatus.valueOf(jsonObj.get("status").getAsString());
		}
		if(jsonObj.get("pricingDay") != null) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			try {
				Date date = format.parse(jsonObj.get("pricingDay").getAsString());
				this.pricingDay = date;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(jsonObj.get("deadline") != null) {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			try {
				Date date = format.parse(jsonObj.get("deadline").getAsString());
				this.deadline = date;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(jsonObj.get("engagementCode") != null) {
			this.engagementCode = jsonObj.get("engagementCode").getAsString();
		}
		if(jsonObj.get("comment") != null) {
			this.comment = jsonObj.get("comment").getAsString();
		}
		if(jsonObj.get("contacts") != null) {
			this.contacts = jsonObj.get("contacts").getAsString();
		}
		if(jsonObj.get("hours") != null) {
			this.hours = jsonObj.get("hours").getAsFloat();
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	public JobType getType() {
		return type;
	}

	public void setType(JobType type) {
		this.type = type;
	}

	public Date getPricingDay() {
		return pricingDay;
	}

	public void setPricingDay(Date pricingDay) {
		this.pricingDay = pricingDay;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public User getPreparer() {
		return preparer;
	}

	public void setPreparer(User preparer) {
		this.preparer = preparer;
	}

	public User getReviewer() {
		return reviewer;
	}

	public void setReviewer(User reviewer) {
		this.reviewer = reviewer;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getEngagementCode() {
		return engagementCode;
	}

	public void setEngagementCode(String engagementCode) {
		this.engagementCode = engagementCode;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public float getHours() {
		return hours;
	}

	public void setHours(float hours) {
		this.hours = hours;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(customer).append("\n");
		sb.append(type).append("\n");
		sb.append(pricingDay).append("\n");
		sb.append(currency).append("\n");
		sb.append(preparer).append("\n");
		sb.append(reviewer).append("\n");
		sb.append(deadline).append("\n");
		sb.append(status).append("\n");
		sb.append(engagementCode).append("\n");
		sb.append(contacts).append("\n");
		sb.append(hours).append("\n");
		return sb.toString();
	}
	

}
