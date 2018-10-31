package model;

public class OutputMessage {
	
	private Job job;
	private String outputPath;
	private String priceCategory;
	private boolean bloomberg;
	private boolean idx;
	private boolean nav;
	private boolean isp;
	private boolean markit;
	private int status;
	
	public static final int STATUS_PREPARE = 0;
	public static final int STATUS_REQUEST = 1;
	public static final int STATUS_OUTPUT = 2;
	
	
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	
	public boolean isBloomberg() {
		return bloomberg;
	}
	public void setBloomberg(boolean bloomberg) {
		this.bloomberg = bloomberg;
	}
	public boolean isIsp() {
		return isp;
	}
	public void setIsp(boolean isp) {
		this.isp = isp;
	}
	public boolean isMarkit() {
		return markit;
	}
	public void setMarkit(boolean markit) {
		this.markit = markit;
	}
	public String getPriceCategory() {
		return priceCategory;
	}
	public void setPriceCategory(String priceCategory) {
		this.priceCategory = priceCategory;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isIdx() {
		return idx;
	}
	public void setIdx(boolean idx) {
		this.idx = idx;
	}
	public boolean isNav() {
		return nav;
	}
	public void setNav(boolean nav) {
		this.nav = nav;
	}
	
	

}
