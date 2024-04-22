package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.repository.*;
import cloud.qasino.games.dto.statistics.Statistics;
import cloud.qasino.games.dto.statistics.SubTotalsGame;
import cloud.qasino.games.dto.statistics.Total;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.database.entity.enums.game.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class CalculateHallOfFameAction implements Action<CalculateHallOfFameAction.CalculateHallOfFameActionDTO, EventOutput.Result> {

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
    public EventOutput.Result perform(CalculateHallOfFameActionDTO actionDto) {

        SubTotalsGame subTotalsGame = new SubTotalsGame();
        subTotalsGame.totalNewGames =
                gameRepository.countByStates(GameState.setupGameStatesValues);
        // TODO diff for other games
        subTotalsGame.totalStartedGames =
                gameRepository.countByStates(GameState.highlowGameStatesValues);
        subTotalsGame.totalsFinishedGames =
                gameRepository.countByStates(GameState.finishedGameStatesValues);

        Total totals = new Total();
        totals.setSubTotalsGames(subTotalsGame);
        totals.totalLeagues = ((int) leagueRepository.count());
        totals.totalVisitors = ((int) visitorRepository.count());
        totals.totalGames = ((int) gameRepository.count());
        totals.totalPlayers = ((int) playerRepository.count());
        totals.totalCards = ((int) cardRepository.count());

        Statistics counters = new Statistics();
        counters.setTotals(Collections.singletonList(totals));
        actionDto.setStatistics(counters);

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
    }

    public interface CalculateHallOfFameActionDTO {

        void setStatistics(Statistics statistics);

        // error setters
        void setHttpStatus(int status);

        void setKey(String key);

        void setValue(String value);

        void setErrorMessage(String key);

        // @formatter:on
    }
}

