package applyextra.commons.action;

import nl.ing.api.party.domain.Arrangement;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class DetermineStudentOverdraftAction implements Action<DetermineStudentOverdraftAction.DetermineStudentOverdraftActionDTO,Boolean>{

    public Boolean perform(DetermineStudentOverdraftActionDTO flowDTO) {
        flowDTO.setStudentOverdraft(!flowDTO.getStudentOverdraftArrangements().isEmpty());
        return true;
    }

    public interface DetermineStudentOverdraftActionDTO {
        List<Arrangement> getStudentOverdraftArrangements();

        void setStudentOverdraft(Boolean isInOverdraft);
    }
}