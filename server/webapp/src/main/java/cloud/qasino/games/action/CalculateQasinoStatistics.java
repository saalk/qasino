package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.statistics.Statistic;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalculateQasinoStatistics implements Action<CalculateQasinoStatistics.Dto, EventOutput.Result> {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private ResultsRepository resultsRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        List<Statistic> statistics = new ArrayList<>();

        statistics.add(new Statistic("total","Games","State:SETUP",gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.SETUP))));
        statistics.add(new Statistic("total","Games","State:PREPARED",gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PREPARED))));
        statistics.add(new Statistic("total","Games","State:PLAYING",gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PLAYING))));
        statistics.add(new Statistic("total","Games","State:FINISHED",gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.FINISHED))));

//        statistics.add(new Statistic("total","Games","All",(int) gameRepository.count()));
        statistics.add(new Statistic("total","Visitors","All",(int) visitorRepository.count()));
        statistics.add(new Statistic("total","Players","AiLevel:HUMAN",(int) playerRepository.countByAiLevel("true","HUMAN")));
        statistics.add(new Statistic("total","Players","AiLevel:DUMB",(int) playerRepository.countByAiLevel("false","DUMB")));
        statistics.add(new Statistic("total","Players","AiLevel:AVERAGE",(int) playerRepository.countByAiLevel("false","AVERAGE")));
        statistics.add(new Statistic("total","Players","AiLevel:SMART",(int) playerRepository.countByAiLevel("false","SMART")));
        statistics.add(new Statistic("total","Cards","All",(int) cardRepository.count()));
        statistics.add(new Statistic("total","Leagues","All",(int) leagueRepository.count()));

        actionDto.setStatistics(statistics);

        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        void setStatistics(List<Statistic> statistics);

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

