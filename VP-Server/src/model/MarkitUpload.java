package model;


import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("MARKIT")
public class MarkitUpload extends Upload {
	
	@Transient
	private String markitRequest;
	
	@Transient
	private List<String> fields;
	
	

	@Override
	public void importMarketDataReply() throws Exception {
		JDBCUtil.createMarkitTable(fields, getUserName());
		JDBCUtil.importCsvBcp(getSqlCompatibleReplyPath(), ",", getUserName());
		JDBCUtil.importMarkitTable(GlobalConstants.BLOOMBERG_MARKIT_TABLE, getUserName());
	}

}
