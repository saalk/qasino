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
import cloud.qasino.games.dto.view.statistics.Statistic;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CalculateQasinoStatisticsAction implements Action<CalculateQasinoStatisticsAction.Dto, EventOutput.Result> {

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

        // @formatter:off
        List<Statistic> statistics = new ArrayList<>();
        long initiator = actionDto.getSuppliedVisitorId();
        statistics.add(new Statistic("Games","State:SETUP",
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.SETUP)),
                gameRepository.countByStatesForInitiator(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.SETUP),initiator)
                ));
        statistics.add(new Statistic("Games","State:PREPARED",
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PREPARED)),
                gameRepository.countByStatesForInitiator(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PREPARED),initiator)
        ));
        statistics.add(new Statistic("Games","State:PLAYING",
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PLAYING)),
                gameRepository.countByStatesForInitiator(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PLAYING),initiator)
                ));
        statistics.add(new Statistic("Games","State:FINISHED",
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.FINISHED)),
                gameRepository.countByStatesForInitiator(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.FINISHED),initiator)
                ));

//      statistics.add(new Statistic("total","Games","All",(int) gameRepository.count()));
        statistics.add(new Statistic("Visitors","All",
                (int) visitorRepository.count(),
                1
                ));
        statistics.add(new Statistic("Players","AiLevel:HUMAN",
                playerRepository.countByAiLevel("true","HUMAN"),
                playerRepository.countByAiLevelForInitiator("true","HUMAN",String.valueOf(initiator))
                ));
        statistics.add(new Statistic("Players","AiLevel:DUMB",
                playerRepository.countByAiLevel("false","DUMB"),
                playerRepository.countByAiLevelForInitiator("false","DUMB",String.valueOf(initiator))
                ));
        statistics.add(new Statistic("Players","AiLevel:AVERAGE",
                playerRepository.countByAiLevel("false","AVERAGE"),
                playerRepository.countByAiLevelForInitiator("false","AVERAGE",String.valueOf(initiator))
                ));
        statistics.add(new Statistic("Players","AiLevel:SMART",
                playerRepository.countByAiLevel("false","SMART"),
                playerRepository.countByAiLevelForInitiator("false","SMART",String.valueOf(initiator))
                ));
        statistics.add(new Statistic("Cards","All",
                (int) cardRepository.count(),
                cardRepository.countCardsForInitiator(String.valueOf(initiator))
                ));
        statistics.add(new Statistic("Leagues","All",
                (int) leagueRepository.count(),
                leagueRepository.countLeaguesForInitiator(String.valueOf(initiator))
                ));

        actionDto.setStatistics(statistics);

        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        void setStatistics(List<Statistic> statistics);
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        long getSuppliedVisitorId();
        TurnEvent getSuppliedTurnEvent();

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

