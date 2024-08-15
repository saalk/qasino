package cloud.qasino.games.action.old;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class CreateNewLeagueAction implements Action<CreateNewLeagueAction.Dto, EventOutput.Result> {

    @Resource
    LeagueRepository leagueRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!(StringUtils.isEmpty(actionDto.getSuppliedLeagueName()))) {
            int sequence = Math.toIntExact(leagueRepository.countByName(actionDto.getSuppliedLeagueName()));
            if (sequence != 0) {
                setConflictErrorMessage(actionDto, "leagueName", String.valueOf(actionDto.getSuppliedLeagueName()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split leagueName and number
            League createdLeague = leagueRepository.save(new League(
                    actionDto.getQasinoVisitor(),
                    actionDto.getSuppliedLeagueName(),
                    1));
            actionDto.setSuppliedLeagueId(createdLeague.getLeagueId());
        } else {
            setBadRequestErrorMessage(actionDto, "leagueName", String.valueOf(actionDto.getSuppliedLeagueName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Supplied value for leagueName is empty");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("leagueName [" + value + "] not available any more");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();
        // Getters
        String getSuppliedLeagueName();
        Visitor getQasinoVisitor();

        // Setter
        void setSuppliedLeagueId(long id);

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
