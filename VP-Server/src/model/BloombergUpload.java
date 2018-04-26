package model;


import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


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


	@Override
	public void importMarketDataReply() throws Exception {
		BloombergRequestType type = bloombergRequest.getBloombergRequestType();
		if(type == BloombergRequestType.BVAL) {
			JDBCUtil.createBVALTable(fieldNamesWithoutDate, getUserName());
			JDBCUtil.importCsvBcp(bloombergSqlCompatibleReplyPath, ";", getUserName());
			JDBCUtil.importBVALTable(bloombergRequest.getBVALPricingDay(), GlobalConstants.BLOOMBERG_PRICING_TABLE, super.getUserName());
		}
		else if(type == BloombergRequestType.DL_PRICING){
			JDBCUtil.createDLPricingTable(fieldNamesWithDate, fieldNamesWithoutDate, getUserName());
			JDBCUtil.importCsvBcp(bloombergSqlCompatibleReplyPath, ";", getUserName());
			JDBCUtil.importDLPricingTable(GlobalConstants.BLOOMBERG_PRICING_TABLE, fieldNamesWithoutDate, super.getUserName());
		}
		else if(type == BloombergRequestType.DL_MASTER) {
			JDBCUtil.createDLMasterTable(fieldNamesWithoutDate, getUserName());
			JDBCUtil.importCsvBcp(bloombergSqlCompatibleReplyPath, ";", getUserName());
			JDBCUtil.importDLMasterTable(GlobalConstants.BLOOMBERG_MASTER_TABLE, getUserName());
		}
		else if(type == BloombergRequestType.DL_HISTIDX || type == BloombergRequestType.DL_HISTNAV) {
			JDBCUtil.createHistoricalTable(fieldNamesWithoutDate, getUserName());
			JDBCUtil.importCsvBcp(bloombergSqlCompatibleReplyPath, ";", getUserName());
			JDBCUtil.importHistoricalTable(GlobalConstants.BLOOMBERG_PRICING_TABLE, getUserName());
		}
		
	}




	
}
