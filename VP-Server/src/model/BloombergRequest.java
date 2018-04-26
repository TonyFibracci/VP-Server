package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.StringUtil;


public class BloombergRequest {
	
	private Map<String, String> header;
	
	private List<String> fields;
	
	private List<String> items;
	
	private boolean valid;
	
	
	public BloombergRequest() {
		header = new HashMap<String, String>();
		fields = new ArrayList<String>();
		items = new ArrayList<String>();
		valid = false;
	}
	
	public BloombergRequest(String fileContent) {
		header = new HashMap<String, String>();
		fields = new ArrayList<String>();
		items = new ArrayList<String>();
		valid = false;
		
		int state = -1;
		final int DEFAULT_STATE = -1;
		final int HEADER_STATE = 0;
		final int FIELD_STATE = 1;
		final int ITEM_STATE = 2;
		boolean hasHeader = false;
		boolean hasFields = false;
		boolean hasItems = false;
		boolean isFinished = false;
		
		BufferedReader bufReader = new BufferedReader(new StringReader(fileContent));
		String line = null;
		try {
			while((line=bufReader.readLine()) != null ){
				if(line.trim().equalsIgnoreCase("START-OF-FILE")) {
					state = HEADER_STATE;
					hasHeader = true;
					continue;
				}
				
				if(line.trim().equalsIgnoreCase("START-OF-FIELDS")) {
					state = FIELD_STATE;
					hasFields = true;
					continue;
				}
				
				if(line.trim().equalsIgnoreCase("START-OF-DATA")) {
					state = ITEM_STATE;
					hasItems = true;
					continue;
				}
				
				if(line.trim().equalsIgnoreCase("END-OF-FIELDS")) {
					state = DEFAULT_STATE;
					continue;
				}
				
				if(line.trim().equalsIgnoreCase("END-OF-DATA")) {
					state = DEFAULT_STATE;
					continue;
				}
				
				if(line.trim().equalsIgnoreCase("END-OF-FILE")) {
					isFinished = true;
					state = DEFAULT_STATE;
				}
				
				if(state == HEADER_STATE) {
					if(!line.trim().equalsIgnoreCase("")) {
						String[] headerData = line.split("=");
						header.put(headerData[0].trim(), headerData[1].trim());
					}
				}
				else if(state == FIELD_STATE) {
					if(!line.trim().equalsIgnoreCase("")) {
						fields.add(line.trim());
					}
				}
				else if(state == ITEM_STATE) {
					if(!line.trim().equalsIgnoreCase("") && !line.contains("#")) {
						items.add(line.trim());
					}
				}
			}
			if(hasHeader && hasFields && hasItems && isFinished) {
				valid = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getProgramName() {
		return header.get("PROGRAMNAME");
	}
	
	public String getBVALPricingDay() {
		for(String field : fields) {
			if(field.contains("BVAL_MID_PRICE")) {
				String[] splits = field.split(":");
				return StringUtil.addSeparatorToDataString(splits[1], "-");
			}
		}
		return "";		
	}
	
	public BloombergRequestType getBloombergRequestType() {
		String programName = getProgramName();
		if(programName == null)
			return null;
		else if(programName.equalsIgnoreCase("getdata")) {
			if(getFields().contains("CRNCY"))
				return BloombergRequestType.DL_MASTER;
			else if(getFields().contains("BVAL_SNAPSHOT"))
				return BloombergRequestType.BVAL;
			else 
				return BloombergRequestType.DL_PRICING;
		}
		else if(programName.equalsIgnoreCase("gethistory")) {
			if(getFields().contains("IDX_RATIO"))
				return BloombergRequestType.DL_HISTIDX;
			else
				return BloombergRequestType.DL_HISTNAV;
		}
		else
			return null;
	}
	
	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	

}
