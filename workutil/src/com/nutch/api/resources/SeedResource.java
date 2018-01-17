package com.nutch.api.resources;

import com.google.common.io.Files;
import com.nutch.api.model.request.SeedList;
import com.nutch.api.model.request.SeedUrl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Collection;

@Path("/seed")
public class SeedResource extends AbstractResource{
    private static final Logger log = LoggerFactory.getLogger(SeedResource.class);

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public String createSeedFile(SeedList seedList) {
        if (seedList == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Seed list cannot be empty!").build());
        }
        File seedFile = createSeedFile();
        BufferedWriter writer = getWriter(seedFile);
        Collection<SeedUrl> seedUrls = seedList.getSeedUrls();
        if (CollectionUtils.isNotEmpty(seedUrls)) {
            for (SeedUrl seedUrl : seedUrls) {
                writeUrl(writer, seedUrl);
            }
        }
        return seedFile.getParent();
    }

    private void writeUrl(BufferedWriter writer, SeedUrl seedUrl) {
        try {
            writer.write(seedUrl.getUrl());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw handleException(e);
        }
    }

    private BufferedWriter getWriter(File seedFile) {
        try {
            return new BufferedWriter(new FileWriter(seedFile));
        } catch (FileNotFoundException e) {
            throw handleException(e);
        } catch (IOException e) {
            throw handleException(e);
        }
    }

    private File createSeedFile() {
        try {
            return File.createTempFile("seed", ".txt", Files.createTempDir());
        }catch (IOException e) {
            throw handleException(e);
        }
    }

    private RuntimeException handleException(Exception e) {
        log.error("Cannot create seed file!", e);
        return new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Cannot create seed file!").build());
    }
}
