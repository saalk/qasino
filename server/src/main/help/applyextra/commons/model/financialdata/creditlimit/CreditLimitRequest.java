package applyextra.commons.model.financialdata.creditlimit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.financialdata.validation.CreditLimitRequestValidator;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditLimitRequest {

    /** type of playingcard: Charge or Revolving */
    @NotNull
    private PortfolioCode portfolioCode;

    @NotNull
    private BigDecimal requestedCreditLimit;

    @Valid
    private CreditLimitFinance finance;

    @Valid
    @Setter(value = AccessLevel.NONE)
    private final CreditLimitRequestValidator validator;

    public CreditLimitRequest() {
        validator = new CreditLimitRequestValidator(this);
    }
}
