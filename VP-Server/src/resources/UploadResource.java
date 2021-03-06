package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.UploadJsonObject;
import service.UploadService;



@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UploadResource {
	
	@GET
	public List<UploadJsonObject> getUploads(@PathParam("jobId") int jobId){
		return UploadService.getUploadsByJobId(jobId);
	}

	@POST
	public void addUpload(UploadJsonObject upload) {
		UploadService.addUpload(upload);
	}
}
