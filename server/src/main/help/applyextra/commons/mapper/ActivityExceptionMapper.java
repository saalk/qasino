package applyextra.commons.mapper;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import nl.ing.riaf.presentation.rest.JsonWebException;
import nl.ing.riaf.presentation.rest.providers.exception.ProblemCodeExceptionMapper;

import javax.ws.rs.core.Response;

@Slf4j
public class ActivityExceptionMapper extends ProblemCodeExceptionMapper<ActivityException> {
	@Override
	public Response toResponse(final ActivityException exception) {
		log.error("Caught ActivityException from application with exception: ", exception);
		return convertExceptionToDetailsMessage(new JsonWebException(exception.getErrorCode(), exception.getServiceName()));
	}
}