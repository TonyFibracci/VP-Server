package resources;

import java.io.IOException;
import java.io.InputStream;
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

import org.glassfish.jersey.media.multipart.FormDataParam;

import model.InputMessage;
import model.OutputMessage;
import service.InputLoader;
import service.OutputWriter;

@Path("/input")
public class InputResource {

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response postInput(
			@FormDataParam("message") InputMessage message, 
			@FormDataParam("file") InputStream fileInputStream){
		try {
			new InputLoader(message, fileInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.OK).build();	
	}
	
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	@POST
//	public Response postInput(InputMessage message){
//		try {
//			new InputLoader(message);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return Response.status(Status.OK).build();	
//	}
	
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	@GET
	@Path("/{userName}")
	public String getActiveInput(@PathParam("userName") String userName) {
		return InputLoader.getActiveInput(userName);
	}
//	
//	
//	@DELETE
//	@Path("/{jobId}")
//	public void deleteJob(@PathParam("jobId") int id) {
//		JobService.deleteJob(id);
//	}
}
