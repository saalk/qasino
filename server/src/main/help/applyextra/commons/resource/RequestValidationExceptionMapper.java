package applyextra.commons.resource;

import applyextra.commons.dao.request.InvalidRequestException;
import applyextra.operations.api.CreditCardResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Created by cl94wq on 22-02-16.
 */
public class RequestValidationExceptionMapper implements ExceptionMapper<InvalidRequestException> {

	@Override
	public Response toResponse(InvalidRequestException e) {
		CreditCardResponse response = new CreditCardResponse();
		response.setErrorCode(ResourceConstants.ERROR_CODE_INVALID_REQUEST);
		return Response.status(Response.Status.FORBIDDEN).entity(response).build();
	}
}
