package cloud.qasino.games.action.visitor;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.service.VisitorService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UpdateVisitorAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource VisitorService visitorService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!(StringUtils.isEmpty(qasino.getCreation().getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorService.countByAlias(qasino.getCreation().getSuppliedAlias()));
            if (sequence != 0 &&
                    !qasino.getCreation().getSuppliedAlias().equals(qasino.getVisitor().getAlias())) {
                qasino.getMessage().setConflictErrorMessage("alias", qasino.getCreation().getSuppliedAlias(), "alias [" + qasino.getCreation().getSuppliedAlias() + "] not available any more");
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split alias and number
        }
        qasino.setVisitor(visitorService.updateUser(qasino.getParams(), qasino.getCreation()));
        return EventOutput.Result.SUCCESS;
    }

}
