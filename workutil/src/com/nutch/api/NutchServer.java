package com.nutch.api;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.nutch.api.impl.JobFactory;
import com.nutch.api.impl.NutchServerPoolExecutor;
import com.nutch.api.impl.RAMConfManager;
import com.nutch.api.impl.RAMJobManager;
import com.nutch.api.misc.ErrorStatusService;
import com.nutch.api.model.response.JobInfo;
import com.nutch.api.resources.*;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.ext.jaxrs.JaxRsApplication;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class NutchServer extends Application {

    public static final String NUTCH_SERVER = "NUTCH_SERVER";
    private static final Logger LOG  = LoggerFactory.getLogger(NutchServer.class);
    private static final String LOCALHOST = "localhost";
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final Integer DEFAULT_PORT = 8081;
    private static final int JOB_CAPACITY = 100;
    private static String logLevel = DEFAULT_LOG_LEVEL;
    private static Integer port = DEFAULT_PORT;
    private static final String CMD_HELP = "help";
    private static final String CMD_STOP = "stop";
    private static final String CMD_PORT = "port";
    private static final String CMD_LOG_LEVEL = "log";

    private Component component;
    private ConfManager confManager;
    private JobManager jobMgr;
    private long started;
    private boolean running;

    public NutchServer() {
        confManager = new RAMConfManager();
        BlockingQueue<Runnable> runnables = Queues.newArrayBlockingQueue(JOB_CAPACITY);
        NutchServerPoolExecutor executor = new NutchServerPoolExecutor(10, JOB_CAPACITY, 1, TimeUnit.HOURS, runnables);
        jobMgr = new RAMJobManager(new JobFactory(), executor, confManager);
        //create a new component
        component = new Component();
        component.getLogger().setLevel(Level.parse(logLevel));
        //add a new http server listening on defined port
        component.getServers().add(Protocol.HTTP, port);
        Context childContext = component.getContext().createChildContext();
        JaxRsApplication application = new JaxRsApplication(childContext);
        application.add(this);
        application.setStatusService(new ErrorStatusService());
        childContext.getAttributes().put(NUTCH_SERVER, this);
        component.getDefaultHost().attach(application);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = Sets.newHashSet();
        resources.add(JobResource.class);
        resources.add(AdminResource.class);
        resources.add(ConfigResource.class);
        resources.add(DbResource.class);
        resources.add(SeedResource.class);
        return resources;
    }

    public void start() {
        LOG.info("Starting NutchServer on port: {} with logging level: {} ...",
                port, logLevel);
        try {
            component.start();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot start server!", e);
        }
        LOG.info("Started NutchServer on port {}", port);
        running = true;
        started = System.currentTimeMillis();
    }

    public boolean canStop(boolean force) {
        if (force) {
            return true;
        }
        Collection<JobInfo> jobInfos = getJobMgr().list(null, JobInfo.State.RUNNING);
        return jobInfos.isEmpty();
    }

    public boolean stop(boolean force) {
        if (!running) {
            return true;
        }
        if (!canStop(force)) {
            LOG.warn("Running jobs - can't stop now.");
            return false;
        }
        LOG.info("Stopping NutchServer on port {}...", port);
        try {
            component.stop();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot stop Nutch Server", e);
        }
        LOG.info("Stopped NutchServer on port {}", port);
        running = false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = createOptions();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.hasOption(CMD_HELP)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("NutchServer", options, true);
            return;
        }

        if (commandLine.hasOption(CMD_LOG_LEVEL)) {
            logLevel = commandLine.getOptionValue(CMD_LOG_LEVEL);
        }
        if (commandLine.hasOption(CMD_PORT)) {
            port = Integer.parseInt(commandLine.getOptionValue(CMD_PORT));
        }
        if (commandLine.hasOption(CMD_STOP)) {
            String stop = commandLine.getOptionValue(CMD_STOP);
            boolean force = StringUtils.equals("force", stop);
            stopRemoteServer(force);
            return;
        }
        startServer();
    }

    private static void startServer() {
        NutchServer server = new NutchServer();
        server.start();
    }

    private static void stopRemoteServer(boolean force) {
        Reference reference = new Reference(Protocol.HTTP, LOCALHOST, port);
        reference.setPath("/admin/stop");
        if (force) {
            reference.addQueryParameter("force", "true");
        }
        ClientResource clientResource = new ClientResource(reference);
        clientResource.get();
    }

    private static Options createOptions() {
        Options options = new Options();
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("logging level");
        OptionBuilder.withDescription("Select a logging level for the NutchServe: \n" +
                "ALl|CONFIG|FINER|FINEST|INFO|OFF|SEVERE|WARNING");
        options.addOption(OptionBuilder.create(CMD_LOG_LEVEL));
        OptionBuilder.withDescription("Stop running NutchServer. true value forces the Server to stop despite" +
                " running jobs e.g. kills the tasks ");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withArgName("force");
        options.addOption(OptionBuilder.create(CMD_STOP));
        OptionBuilder.withDescription("Show this help");
        options.addOption(OptionBuilder.create(CMD_HELP));

        OptionBuilder.withDescription("Port to use for restful API.");
        OptionBuilder.hasOptionalArg();
        OptionBuilder.withArgName("port number");
        options.addOption(OptionBuilder.create(CMD_PORT));
        return options;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public ConfManager getConfMgr() {
        return confManager;
    }

    public void setConfMgr(ConfManager confManager) {
        this.confManager = confManager;
    }

    public JobManager getJobMgr() {
        return jobMgr;
    }

    public void setJobMgr(JobManager jobMgr) {
        this.jobMgr = jobMgr;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
