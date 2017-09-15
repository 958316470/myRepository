package com.nutch.api.misc;

import com.nutch.api.model.response.ErrorResponse;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

public class ErrorStatusService extends StatusService {

    @Override
    public Representation getRepresentation(Status status, Request request, Response response) {
        ErrorResponse errorResponse = new ErrorResponse(status.getThrowable());
        return new JacksonRepresentation<ErrorResponse>(errorResponse);
    }

    @Override
    public Status getStatus(Throwable throwable, Request request, Response response) {
        return new Status(Status.SERVER_ERROR_INTERNAL,throwable);
    }
}
