package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.AccountCategory;
import applyextra.commons.model.PortfolioCode;
import applyextra.commons.model.RepricingProgram;
import applyextra.operations.model.SIACreditCard;
import nl.ing.serviceclient.sia.inquireaccount2.jaxb.generated.DailyFinancialsResponseItem;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SIAAccountInformation {

    private List<RepricingProgram> repricingPrograms;
    private String siaAccountNumber;
    private String siaAccountStatus;
    private AccountCategory accountCategory;
    private PortfolioCode portfolioCode;
    private final List<SIACreditCard> siaCards = new ArrayList<>();
    private Integer creditLineAmount;
    private DailyFinancialsResponseItem dailyFinancialsResponseItem;

}
