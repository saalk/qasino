package applyextra.commons.resource;

import applyextra.operations.api.CreditCardResponse;
import org.springframework.dao.ConcurrencyFailureException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Custom exception handler to handle ConcurrencyFailureException , created by cl94wq on 26-01-16.
 */
@Provider
public class ConcurrencyFailureExceptionMapper implements ExceptionMapper<ConcurrencyFailureException> {

  @Override
  public Response toResponse(ConcurrencyFailureException e) {
    CreditCardResponse response = new CreditCardResponse();
    response.setErrorCode(ResourceConstants.ERROR_CODE_CONCURRENT_REQUEST);
    return Response.status(Response.Status.OK).entity(response).build();
  }

}
