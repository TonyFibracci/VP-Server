package model;


import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@DiscriminatorValue("BLOOMBERG")
public class BloombergUpload extends Upload {
	
	@Transient
	private BloombergRequest bloombergRequest;
	
	@Transient
	private String bloombergSqlCompatibleReplyPath;
	
	@Transient
	private List<String> fieldNamesWithDate;
	
	@Transient
	private List<String> fieldNamesWithoutDate;
	
	
	@Override
	public String toString() {
		String res = super.toString();
		res += "Provider: Bloomberg\n";
		return res;
	}


	public BloombergRequest getBloombergRequest() {
		return bloombergRequest;
	}





	
}
