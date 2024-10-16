package cloud.qasino.games.action.league;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.service.LeaguesService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@Component
public class CreateNewLeagueAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource LeaguesService leaguesService;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!(StringUtils.isEmpty(qasino.getCreation().getSuppliedLeagueName()))) {
            int sequence = Math.toIntExact(leaguesService.countByName(qasino.getCreation().getSuppliedLeagueName()));
            if (sequence != 0) {
                qasino.getMessage().setConflictErrorMessage("leagueName",String.valueOf(qasino.getCreation().getSuppliedLeagueName()),"leagueName [" + qasino.getCreation().getSuppliedLeagueName() + "] not available any more");
                return EventOutput.Result.FAILURE;
            }
            Visitor visitor = VisitorMapper.INSTANCE.fromDto(qasino.getVisitor());
            // todo LOW split leagueName and number
            LeagueDto createdLeague = leaguesService.saveNewLeague(new League(
                    visitor,
                    qasino.getCreation().getSuppliedLeagueName(),
                    1));
            qasino.setLeague(createdLeague);
            qasino.getParams().setSuppliedLeagueId(createdLeague.getLeagueId());
        } else {
            qasino.getMessage().setBadRequestErrorMessage("leagueName", String.valueOf(qasino.getCreation().getSuppliedLeagueName()),"Supplied value for leagueName is empty");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
}
