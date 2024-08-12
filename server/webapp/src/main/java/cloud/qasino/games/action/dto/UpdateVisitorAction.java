package cloud.qasino.games.action.dto;

import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateVisitorAction extends ActionDto<EventOutput.Result> {

    // formatter:off
    @Resource
    VisitorRepository visitorRepository;
    @Resource
    VisitorAndLeaguesService visitorAndLeaguesService;
    // formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!(StringUtils.isEmpty(qasino.getCreation().getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(qasino.getCreation().getSuppliedAlias()));
            if (sequence != 0) {
                setConflictErrorMessage(qasino, "alias", String.valueOf(qasino.getCreation().getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split alias and number
            qasino.setVisitor(visitorAndLeaguesService.updateUser(qasino.getParams().getSuppliedVisitorId(), qasino.getCreation()));
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("username [" + value + "] not available any more");
    }
}
