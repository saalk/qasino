package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class FindVisitorIdByAliasAction implements Action<FindVisitorIdByAliasAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // set http to created when done
        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence > 1) {
                // todo LOW split alias and number
                setConflictErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            } else if (sequence == 0) {
                setNotFoundErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            Optional<Visitor> foundVisitor = visitorRepository.findVisitorByAliasAndAliasSequence(actionDto.getSuppliedAlias(), 1);
            if (foundVisitor.isPresent()) {
                actionDto.setSuppliedVisitorId(foundVisitor.get().getVisitorId());
            } else {
                setNotFoundErrorMessage(actionDto, "visitorId", actionDto.getSuppliedAlias());
                return EventOutput.Result.FAILURE;
            }
        } else {
            setBadRequestErrorMessage(actionDto, "Visitor", String.valueOf(actionDto.getSuppliedAlias()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setNotFoundErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setNotFoundErrorMessage("");
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Multiple " + id + " found for supplied value [" + value +
                "]");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        String getSuppliedAlias();

        // Setter
        void setSuppliedVisitorId(long id);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}