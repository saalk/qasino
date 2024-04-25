package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class CreateNewLeagueAction implements Action<CreateNewLeagueAction.CreateNewLeagueActionDTO, EventOutput.Result> {

    @Resource
    LeagueRepository leagueRepository;

    @Override
    public EventOutput.Result perform(CreateNewLeagueActionDTO actionDto) {

        if (!(StringUtils.isEmpty(actionDto.getSuppliedLeagueName()))) {
            int sequence = Math.toIntExact(leagueRepository.countByName(actionDto.getSuppliedLeagueName()));
            if (sequence != 0) {
                setErrorMessageConflict(actionDto, "leagueName", String.valueOf(actionDto.getSuppliedLeagueName()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split leagueName and number
            League createdLeague = leagueRepository.save(new League(
                    actionDto.getQasinoVisitor(),
                    actionDto.getSuppliedLeagueName(),
                    1));
            if (createdLeague.getLeagueId() == 0) {
                setErrorMessageInternalServerError(actionDto, "leagueName", String.valueOf(actionDto.getSuppliedLeagueName()));
                return EventOutput.Result.FAILURE;
            }
            actionDto.setSuppliedLeagueId(createdLeague.getLeagueId());
        } else {
            setErrorMessageBadRequest(actionDto, "leagueName", String.valueOf(actionDto.getSuppliedLeagueName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageBadRequest(CreateNewLeagueActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value for leagueName is empty");
    }


    private void setErrorMessageConflict(CreateNewLeagueActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("leagueName [" + value + "] not available any more");
    }

    private void setErrorMessageInternalServerError(CreateNewLeagueActionDTO actionDto, String id,
                                                    String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Crash while creating a new league");
    }

    public interface CreateNewLeagueActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedLeagueName();
        Visitor getQasinoVisitor();

        // Setter
        void setSuppliedLeagueId(long id);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
