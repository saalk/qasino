package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.dto.statistics.Statistics;
import cloud.qasino.games.dto.statistics.SubTotalsGame;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CountQasinoTotals implements Action<CountQasinoTotals.Dto, EventOutput.Result> {

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

        SubTotalsGame subTotalsGame = new SubTotalsGame();
        subTotalsGame.totalGamesSetup =
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.SETUP));
        subTotalsGame.totalGamesPrepared =
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PREPARED));
        subTotalsGame.totalGamesPlaying =
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.PLAYING));
        subTotalsGame.totalsGamesFinished =
                gameRepository.countByStates(GameStateGroup.listGameStatesStringsForGameStateGroup(GameStateGroup.FINISHED));

        Statistics statistics = new Statistics();
        statistics.setSubTotalsGames(subTotalsGame);
        statistics.totalLeagues = ((int) leagueRepository.count());
        statistics.totalVisitors = ((int) visitorRepository.count());
        statistics.totalGames = ((int) gameRepository.count());
        statistics.totalPlayers = ((int) playerRepository.count());
        statistics.totalCards = ((int) cardRepository.count());

        actionDto.setStatistics(statistics);

        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(SetStatusIndicatorsBaseOnRetrievedDataAction.SetStatusIndicatorsBaseOnRetrievedDataDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + id);
    }

    public interface Dto {

        void setStatistics(Statistics statistics);

        // error setters
        void setHttpStatus(int status);

        void setErrorKey(String errorKey);

        void setErrorValue(String errorValue);

        void setErrorMessage(String key);

        // @formatter:on
    }
}

