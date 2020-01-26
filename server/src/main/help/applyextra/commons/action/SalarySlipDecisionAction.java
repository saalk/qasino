package applyextra.commons.action;

import applyextra.commons.event.EventOutput;
import applyextra.commons.model.SalarySlipDecision;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

@Component
public class SalarySlipDecisionAction implements Action<SalarySlipDecisionAction.SalarySlipDecisionActionDTO, EventOutput> {

    @Override
    public EventOutput perform(final SalarySlipDecisionActionDTO flowDTO) {
        SalarySlipDecision salarySlipDecision = flowDTO.getSalarySlipDecision();
        if (salarySlipDecision != null && salarySlipDecision.isApproved()) {
            return EventOutput.success();
        }
        return EventOutput.failure();
    }

    public interface SalarySlipDecisionActionDTO {

        SalarySlipDecision getSalarySlipDecision();

    }

}
