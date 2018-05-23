package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.OutputMessage;
import service.OutputWriter;

@Path("/output")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OutputResource {

	@POST
	public Response getOutput(OutputMessage message){
		System.out.println(message.getOutputPath());
		new OutputWriter(message).run();
		return Response.status(Status.OK).build();	
	}
	
//	@POST
//	public void addJob(Job job) {
//		JobService.addJob(job);
//	}
//	
//	
//	@DELETE
//	@Path("/{jobId}")
//	public void deleteJob(@PathParam("jobId") int id) {
//		JobService.deleteJob(id);
//	}
}
