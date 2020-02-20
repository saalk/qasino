package applyextra.commons.action;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.event.EventOutput.Result;
import applyextra.commons.orchestration.Action;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Component
@Lazy
public class CreatePegaCaseDecisionAction implements Action<CreatePegaCaseDecisionAction.CreatePegaCaseDecisionActionDTO,  EventOutput.Result> {


    @Override
    public EventOutput.Result perform(CreatePegaCaseDecisionActionDTO dto) {
        if (dto.getPortfolioCode() != null) {
            if(PortfolioCode.REVOLVING.equals(dto.getPortfolioCode())){
                return  EventOutput.Result.SUCCESS;
            }
            return  EventOutput.Result.FAILURE;
        }
        throw new ActivityException(CreatePegaCaseDecisionAction.class.getCanonicalName(), "Null PortfolioCode");
    }

    public interface CreatePegaCaseDecisionActionDTO {

        PortfolioCode getPortfolioCode();

    }


}
