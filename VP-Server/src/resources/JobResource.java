package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;


import model.Job;
import service.JobService;

@Path("/jobs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JobResource {

	@GET
	@Path("/{user}")
	public List<Job> getJobs(@PathParam("user") String userId){
		return JobService.getAllJobs(userId);		
	}
	
	@POST
	public void addJob(Job job) {
		JobService.addJob(job);
	}
	
	
	@DELETE
	@Path("/{jobId}")
	public void deleteJob(@PathParam("jobId") int id) {
		JobService.deleteJob(id);
	}
	
	@Path("/{jobId}/uploads")
	public UploadResource getUploadResource() {
		return new UploadResource();
	}
}
