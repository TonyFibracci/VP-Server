package resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.User;
import model.VPClient;
import service.ClientService;
import service.UserService;

@Path("/clients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientResource {

	@GET
	public List<VPClient> getClients(){
		return ClientService.getAllClients();
	}
}
