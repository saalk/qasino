package applyextra.commons.mapper;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.dao.request.InvalidRequestException;
import applyextra.commons.resource.ErrorCodes;
import nl.ing.riaf.presentation.rest.JsonWebException;
import nl.ing.riaf.presentation.rest.providers.exception.ProblemCodeExceptionMapper;
import org.springframework.dao.ConcurrencyFailureException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Catches and maps different types of Runtime Exceptions
 *
 * 1. Log the stacktrace regardless of log level;
 * 2. Return a formatted response easily understood by the back office instead of several nulls
 */
@Slf4j
@Provider
public class RuntimeExceptionMapper extends ProblemCodeExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(final RuntimeException exception) {

        log.error("Caught RuntimeException from application with exception: ", exception);

        if (exception instanceof WebApplicationException) {
            return webApplicationExceptionResponse((WebApplicationException) exception);
        } else if (exception instanceof ActivityException) {
            return activityExceptionResponse((ActivityException) exception);
        } else if (exception instanceof ConcurrencyFailureException) {
            return buildResponse(ErrorCodes.CONCURRENT_REQUEST);
        } else if (exception instanceof InvalidRequestException) {
            return buildResponse(ErrorCodes.INVALID_REQUEST);
        } else {
            return buildResponse(ErrorCodes.RUNTIME_EXCEPTION);
        }
    }

    /**
     * Copied from WebApplicationExceptionMapper.class. Should be improved to avoid code duplication
     */
    private Response webApplicationExceptionResponse(final WebApplicationException exception) {
        if (exception.getResponse() != null) {
            return convertExceptionToDetailsMessage(new JsonWebException(exception.getResponse().getStatus()));
        } else {
            return buildResponse(ErrorCodes.WEB_APPLICATION_EXCEPTION);
        }
    }

    /**
     * Copied from ActivityExceptionMapper.class. Should be improved to avoid code duplication
     */
    private Response activityExceptionResponse(final ActivityException exception) {
        return convertExceptionToDetailsMessage(new JsonWebException(exception.getErrorCode(), exception.getServiceName()));
    }

    private Response buildResponse(final ErrorCodes code) {
        return convertExceptionToDetailsMessage(new JsonWebException(code.getStatus()));
    }
}
