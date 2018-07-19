package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.Job;
import model.User;
import model.VPClient;
import service.ClientService;
import service.JobService;
import service.UserService;

@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

	@GET
	public List<VPClient> getClients(){
		return ClientService.getAllClients();
	}
	
	@POST
	public void addClient(VPClient client) {
		ClientService.addClient(client);
	}
	
	@DELETE
	@Path("/{clientId}")
	public void deleteClient(@PathParam("clientId") int id) {
		ClientService.deleteClient(id);
	}
	
	@GET
	@Path("/{clientId}/jobs")
	public List<Job> getClientJobs(@PathParam("clientId") int id) {
		return JobService.getAllClientJobs(id);
	}
}
