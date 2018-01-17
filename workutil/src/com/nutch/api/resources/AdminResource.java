package com.nutch.api.resources;

import com.nutch.api.model.response.JobInfo;
import com.nutch.api.model.response.NutchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Path(value = "/admin")
public class AdminResource extends AbstractResource {
    private static final int DELAY_SEC = 10;
    private static final long DELAY_MILLIS = TimeUnit.SECONDS.toMillis(DELAY_SEC);
    private static final Logger LOG = LoggerFactory.getLogger(AdminResource.class);

    @GET
    @Path("/")
    public NutchStatus getNutchStatus() {
        NutchStatus status = new NutchStatus();
        status.setStartDate(new Date(server.getStarted()));
        status.setConfiguration(confManager.list());
        status.setJobs(jobManager.list(null, JobInfo.State.ANY));
        status.setRunningJobs(jobManager.list(null, JobInfo.State.RUNNING));
        return status;
    }

    @GET
    @Path("/stop")
    public String stop(@QueryParam("force") boolean force) {
        if (!server.canStop(force)) {
            LOG.info("Command 'stop' denied due to unfinished jobs");
            return "Can't stop now. There are jobs running. Try force option.";
        }
        scheduleServerStop();
        return MessageFormat.format("Stopping in {0} seconds", DELAY_SEC);
    }

    private void scheduleServerStop() {
        LOG.info("Server shutdown shceduled in {} seconds", DELAY_SEC);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(DELAY_SEC);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                server.stop(false);
                LOG.info("Service stopped.");
            }
        };
        thread.setDaemon(true);
        thread.start();
        LOG.info("Service shutting down...");
    }
}
