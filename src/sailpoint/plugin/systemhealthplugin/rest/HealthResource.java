package sailpoint.plugin.systemhealthplugin.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sailpoint.api.SailPointContext;
import sailpoint.object.Attributes;
import sailpoint.object.Server;
import sailpoint.plugin.systemhealthplugin.SystemHealthDTO;
import sailpoint.rest.plugin.BasePluginResource;
import sailpoint.rest.plugin.RequiredRight;
import sailpoint.tools.GeneralException;

@RequiredRight("SystemHealthPluginRestServiceAllow")
@Path("systemhealthplugin")
public class HealthResource extends BasePluginResource {
	
	private static Log log = LogFactory.getLog(HealthResource.class);
	
    public HealthResource() {
    	log.info("Constructor: HealthResource");
    }

    /**
     * Get all server hosts in the IdentityIQ installation.
     * 
     * @return
     */
	private List<Server> getHosts() {
		log.debug("Enter: getHosts()");
		SailPointContext context = getContext();
		if (context != null) {
			try {
				return context.getObjects(Server.class);
			} catch (GeneralException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		log.error("Returning an empty list.");
		return new ArrayList<Server>();
	}
	
	/**
	 * Retrieve all server hosts and analyze their status.
	 * 
	 * @return
	 */
	private String getOverallSystemStatus() {
		List<Server> hosts = getHosts();
		String status = "UNKNOWN";
		if (hosts.isEmpty()) {
			log.error("Empty host list - should not happen!");
			status = "ERROR";
		} else {
			status = "OK";
			for (Server host: hosts) {
				if (host.isInactive()) {
					log.error("Inactive host");
					status = "ERROR";
					break;
				}
				Attributes<String, Object> attributes = host.getAttributes();
				if (attributes.getFloat("cpuUsage") > 80.0) {
					log.warn("CPU load greater than 80 percent");
					status = "WARN";
				}
			}
		}
		return status;
	}
    
    /**
     * Returns the system health status
     */
    @RequiredRight("SystemHealthPluginRestServiceAllow")
    @GET
    @Path("getStatus")
    @Produces(MediaType.APPLICATION_JSON)

    public SystemHealthDTO
    getStatus() throws Exception {
    	log.debug("Enter: getStatus()");
        SystemHealthDTO healthDTO = new SystemHealthDTO();
        healthDTO.set_status(getOverallSystemStatus());
        return healthDTO;
    }

	@Override
	public String getPluginName() {
		return "systemhealthplugin";
	}
}
