package com.nutch.api.resources;

import com.nutch.api.ConfManager;
import com.nutch.api.JobManager;
import com.nutch.api.NutchServer;
import org.restlet.Context;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Produces({MediaType.APPLICATION_JSON})
public abstract class AbstractResource {

    protected ConfManager confManager;
    protected JobManager jobManager;
    protected NutchServer server;

    public AbstractResource() {
        server = (NutchServer) Context.getCurrent().getAttributes().get(NutchServer.NUTCH_SERVER);
        confManager = server.getConfMgr();
        jobManager = server.getJobMgr();
    }

    protected void throwBadRequestException(String message) {
        throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(message).build());
    }
}
