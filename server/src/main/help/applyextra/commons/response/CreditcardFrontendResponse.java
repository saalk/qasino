package applyextra.commons.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CreditcardFrontendResponse<T> {
    CreditcardFrontendResponse extract(final T dto);
}
