package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.dto.statistics.Counter;
import cloud.qasino.card.dto.statistics.SubTotalsGame;
import cloud.qasino.card.dto.statistics.Total;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.*;
import cloud.qasino.card.statemachine.GameState;
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
    private UserRepository userRepository;
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

        log.debug("Action: CalculateHallOfFameAction");

        SubTotalsGame subTotalsGame = new SubTotalsGame();
        subTotalsGame.totalNewGames =
                gameRepository.countByStates(GameState.cardGamesNewValues);
        subTotalsGame.totalStartedGames =
                gameRepository.countByStates(GameState.cardGamesStartedValues);
        subTotalsGame.totalsFinishedGames =
                gameRepository.countByStates(GameState.cardGamesFinishedValues);

        Total totals = new Total();
        totals.setSubTotalsGames(subTotalsGame);
        totals.totalLeagues = ((int) leagueRepository.count());
        totals.totalUsers = ((int) userRepository.count());
        totals.totalGames = ((int) gameRepository.count());
        totals.totalPlayers = ((int) playerRepository.count());
        totals.totalCards = ((int) cardRepository.count());

        Counter counters = new Counter();
        counters.setTotals(Collections.singletonList(totals));
        actionDto.setCounter(counters);

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
        actionDto.setUriAndHeaders();
    }

    public interface CalculateHallOfFameActionDTO {

        void setCounter(Counter counter);

        // error setters
        void setHttpStatus(int status);

        void setErrorKey(String key);

        void setErrorValue(String value);

        void setErrorMessage(String key);

        void setUriAndHeaders();
        // @formatter:on
    }
}

