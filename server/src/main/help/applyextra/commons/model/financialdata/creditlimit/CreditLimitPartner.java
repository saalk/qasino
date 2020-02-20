package applyextra.commons.model.financialdata.creditlimit;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.financialdata.PartnerWithFinance;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
public class CreditLimitPartner implements PartnerWithFinance {

    private static final int INITIALS_MAX_LENGTH = 24;
    private static final int LASTNAME_PREFIX_MAX_LENGTH = 15;
    private static final int LASTNAME_MAX_LENGTH = 70;

    /** the encrypted customer id */
    private String id;

    @Size(max = INITIALS_MAX_LENGTH)
    private String initials;

    @Size(max = LASTNAME_PREFIX_MAX_LENGTH)
    private String lastNamePrefix;

    @Size(max = LASTNAME_MAX_LENGTH)
    private String lastName;

    private String birthDate;

    @Valid
    @NotNull
    private CreditLimitPartnerFinance finance;
}