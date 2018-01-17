package com.nutch.api.resources;

import com.nutch.api.model.request.JobConfig;
import com.nutch.api.model.response.JobInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path(value = "/job")
public class JobResource extends AbstractResource {

    @GET
    @Path(value = "/")
    public Collection<JobInfo> getJobs(@QueryParam("crawlId") String crawlId) {
        return jobManager.list(crawlId, JobInfo.State.ANY);
    }

    @GET
    @Path(value = "{id}")
    public JobInfo getInfo(@PathParam("id") String id, @QueryParam("crawlId") String crawlId) {
        return jobManager.get(crawlId, id);
    }

    @GET
    @Path(value = "/{id}/stop")
    public boolean stop(@PathParam("id") String id, @QueryParam("crawlId") String crawlId) {
        return jobManager.stop(crawlId, id);
    }

    @GET
    @Path(value = "/{id}/abort")
    public boolean abort(@PathParam("id") String id, @QueryParam("crawlId") String crawlId) {
        return jobManager.abort(crawlId, id);
    }

    @POST
    @Path(value = "/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public String create(JobConfig config) {
        if (config == null) {
            throwBadRequestException("Job configuration is required!");
        }
        return jobManager.create(config);
    }
}
