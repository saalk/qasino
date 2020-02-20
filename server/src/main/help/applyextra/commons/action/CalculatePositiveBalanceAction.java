package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.api.listaccounts.creditcards.value.CreditCardAccount;
import applyextra.api.listaccounts.creditcards.value.DailyFinancials;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.FilterListAccountsResponseUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
@Lazy
@Slf4j
public class CalculatePositiveBalanceAction implements Action<CalculatePositiveBalanceAction.CalculatePositiveBalanceActionDTO, EventOutput> {

    @Resource
    private FilterListAccountsResponseUtil responseUtil;

    @Override
    public EventOutput perform(CalculatePositiveBalanceActionDTO dto) {
        if(!responseUtil.isSelectedCardPrimaryCard(dto.getCreditCardAccount(), dto.getSelectedCreditCardId())){
            log.info("The Selected PlayingCard is not primary, no positive balance calculation for " + dto.getRequestId());
            return EventOutput.success();
        }
        log.debug("Calculating POSITIVE balance");
        DailyFinancials dailyFinancials = dto.getCreditCardAccount().getDailyFinancials();
        dto.setBalance(
                dailyFinancials.getAvailableCreditAmount().subtract(dto.getCreditCardAccount().getAssignedCreditLimit())
        );
        return EventOutput.success();
    }

    public interface CalculatePositiveBalanceActionDTO{
        CreditCardAccount getCreditCardAccount();
        void setBalance(BigDecimal balance);
        String getRequestId();
        String getSelectedCreditCardId();
    }
}
