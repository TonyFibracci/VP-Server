package resources;

import java.io.IOException;
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

import model.InputMessage;
import model.OutputMessage;
import service.InputLoader;
import service.OutputWriter;

@Path("/input")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InputResource {

	@POST
	public Response postInput(InputMessage message){
		try {
			new InputLoader(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
