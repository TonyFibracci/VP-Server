package resources;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataParam;

import model.InputMessage;
import service.ImportService;

@Path("/import")
public class ImportResource {
	
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	@Path("/isp")
	public Response postISP(
			@FormDataParam("file") InputStream fileInputStream){
		
		try {
			ImportService.importISP(fileInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.OK).build();	
	}
	
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	@Path("/nav")
	public Response postNAV(
			@FormDataParam("user") String userName,
			@FormDataParam("file") InputStream fileInputStream){	
		
		try {
			ImportService.importNAV(fileInputStream, userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.OK).build();	
	}
	
	

}
