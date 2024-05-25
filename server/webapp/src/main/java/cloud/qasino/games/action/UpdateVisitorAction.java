package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateVisitorAction implements Action<UpdateVisitorAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence != 0) {
                setConflictErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split alias and number
            Visitor visitorToUpdate = visitorRepository.findOneByVisitorId(actionDto.getSuppliedVisitorId());
            visitorToUpdate.setAlias(actionDto.getSuppliedAlias());
//            visitorToUpdate.setUsername(actionDto.getSuppliedUsername());
            visitorToUpdate.setEmail(actionDto.getSuppliedEmail());
            actionDto.setSuppliedVisitorId(visitorRepository.save(visitorToUpdate).getVisitorId());
        } else {
            setBadRequestErrorMessage(actionDto, "username", String.valueOf(actionDto.getSuppliedUsername()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

     private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("empty");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("username [" + value + "] not available any more");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        long getSuppliedVisitorId();
        String getSuppliedUsername();
        String getSuppliedPassword();
        String getSuppliedEmail();
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
