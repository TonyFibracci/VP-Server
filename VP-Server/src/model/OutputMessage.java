package model;

public class OutputMessage {
	
	private Job job;
	private String outputPath;
	private boolean bloomberg;
	private boolean isp;
	private boolean markit;
	
	
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
	
	

}
