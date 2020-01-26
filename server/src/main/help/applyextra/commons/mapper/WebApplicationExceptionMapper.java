package applyextra.commons.mapper;

import lombok.extern.slf4j.Slf4j;
import nl.ing.riaf.presentation.rest.JsonWebException;
import nl.ing.riaf.presentation.rest.RiafHttpStatusCodes;
import nl.ing.riaf.presentation.rest.providers.exception.ProblemCodeExceptionMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Slf4j
public class WebApplicationExceptionMapper extends ProblemCodeExceptionMapper<WebApplicationException> {
    private static final int ERROR_CODE = 900;

    @Override
    public Response toResponse(final WebApplicationException exception) {
        log.error("Caught WebApplicationException from application with exception: ", exception);
        if (exception.getResponse() != null) {
            return convertExceptionToDetailsMessage(new JsonWebException(exception.getResponse().getStatus(), RiafHttpStatusCodes.GENERAL_APPLICATION_ERROR));
        } else {
            return convertExceptionToDetailsMessage(new JsonWebException(ERROR_CODE, RiafHttpStatusCodes.GENERAL_APPLICATION_ERROR));
        }
    }
}
