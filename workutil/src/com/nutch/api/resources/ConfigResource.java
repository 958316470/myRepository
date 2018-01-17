package com.nutch.api.resources;

import com.nutch.api.model.request.NutchConfig;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;

@Path("/config")
public class ConfigResource extends AbstractResource {

    public static final String DEFAULT = "default";

    @GET
    @Path("/")
    public Set<String> getConfigs() {
        return confManager.list();
    }
    @GET
    @Path("/{configId}")
    public Map<String, String> getConfig(@PathParam("configId") String configId) {
        return confManager.getAsMap(configId);
    }

    @GET
    @Path("/{configId}/{propertyId}")
    public String getProperty(@PathParam("configId") String configId, @PathParam("propertyId") String propertyId) {
        return confManager.getAsMap(configId).get(propertyId);
    }

    @DELETE
    @Path("/{configId}")
    public void deleteConfig(@PathParam("configId") String configId) {
        confManager.delete(configId);
    }

    @POST
    @Path("/{configId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String createConfig(NutchConfig newConfig) {
        if (newConfig == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Nutch configuration cannot be empty!").build());
        }
        return confManager.create(newConfig);
    }

    @PUT
    @Path("/{config}/{property}")
    public Response update(@PathParam("config") String config, @PathParam("property") String property,
                           @FormParam("value") String value) {
        if (value == null) {
            throwBadRequestException("Missiong property value!");
        }
        confManager.setProperty(config, property, value);
        return Response.ok().build();
    }
}
