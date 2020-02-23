package applyextra.commons.model.financialdata.dto;

import lombok.Getter;
import lombok.Setter;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.commons.action.CheckRequestedCreditLimitAction;
import applyextra.commons.action.GetBKRScoreAction;
import applyextra.commons.action.IndividualsAllAction;
import applyextra.commons.action.ScoringAction;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import applyextra.commons.model.database.entity.*;
import applyextra.commons.model.financialdata.CardType;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public abstract class FinancialAcceptanceDTO extends AbstractCreditcardFlowDTO implements
        IndividualsAllAction.IndividualsAllActionDTO,
        GetBKRScoreAction.GetBKRScoreDTO,
        ScoringAction.ScoringContext,
        CheckRequestedCreditLimitAction.CheckRequestedCreditLimitDTO{
    private PartyEntity partyEntity;
    private DecisionScoreEntity overallDecisionScore;
    private PortfolioCode portfolioCode;
    private IndividualsAllBusinessResponse individualsAll;
    private String externalBureauId;
    private BigDecimal maxLimitAllowed;
    private Integer creditLimit;
    private Integer requestedCreditLimit;
    private boolean creditLimitResult;
    private CardType cardType;

    public void setMaxLimitAllowed(BigDecimal maxLimitAllowed){
        this.maxLimitAllowed = maxLimitAllowed;
        setCreditLimit(maxLimitAllowed.intValue());
    }

    public FinancialDataEntity getFinancialData() { return this.getCreditcardRequest().getFinancialData(); }

    public void setFinancialData(FinancialDataEntity financialData) { this.getCreditcardRequest().setFinancialData(financialData);}

    public List<ExtraLoanEntity> getExtraLoans() {
        return this.getFinancialData().getExtraLoanEntities();
    }
}
