package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.util.ActionUtils;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class CalculateAndFinishGameAction implements Action<CalculateAndFinishGameAction.Dto, EventOutput.Result> {

    @Resource
    CardMoveRepository cardMoveRepository;
    @Resource
    CardRepository cardRepository;
    @Resource
    ResultsRepository resultsRepository;
    @Resource
    VisitorRepository visitorRepository;

    @Autowired
    PlayService playService;

    @Override
    public EventOutput.Result perform(CalculateAndFinishGameAction.Dto actionDto) {

        List<Result> results = resultsRepository.findAllByGame(actionDto.getQasinoGame());
        if (results != null) {
            actionDto.setGameResults(results);
            return EventOutput.Result.SUCCESS;
        } else if (actionDto.getQasinoGame() == null) {
            actionDto.setGameResults(null);
            return EventOutput.Result.SUCCESS;
        } else if (actionDto.getQasinoGame().getState().getGroup() != GameStateGroup.FINISHED) {
            actionDto.setGameResults(null);
            return EventOutput.Result.SUCCESS;
        }

        HashMap<Long, Integer> playerProfit = new HashMap<>();
        List<Player> players = actionDto.getQasinoGame().getPlayers();
        Optional<Visitor> initiator = visitorRepository.findVisitorByVisitorId(actionDto.getQasinoGame().getInitiator());

        for (Player player : players) {
            playerProfit.put(player.getPlayerId(), 0);
        }
        if (actionDto.getAllCardMovesForTheGame() == null || players.isEmpty()) {
            // some error happened - just stop calculating
            return EventOutput.Result.SUCCESS;
        }
        // calculate all players profits
        for (CardMove cardMove : actionDto.getAllCardMovesForTheGame()) {
            int start = playerProfit.get(cardMove.getPlayerId());
            int profit = start + (cardMove.getEndFiches() - cardMove.getStartFiches());
            playerProfit.replace(cardMove.getPlayerId(), profit);
        }
        // rank and create results
        HashMap<Long, Integer> playerProfitSortedOnValue = ActionUtils.sortByValue(playerProfit);

        Player winner = null;
        for (Map.Entry<Long, Integer> en : playerProfitSortedOnValue.entrySet()) {
            Player player = ActionUtils.findPlayerByPlayerId(players, en.getKey());
            boolean won = false;
            if (winner == null) {
                winner = player;
                won = true;
            }
            // en.getKey()
            // en.getValue()
            resultsRepository.save(new Result(
                    player,
                    initiator.get(),
                    actionDto.getQasinoGame(),
                    actionDto.getQasinoGame().getType(),
                    en.getValue(),
                    won
            ));
        }
        actionDto.setGameResults(resultsRepository.findAllByGame(actionDto.getQasinoGame()));
        return EventOutput.Result.SUCCESS;
    }

    void setUnprocessableErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setUnprocessableErrorMessage("Action [" + id + "] invalid, no previous card to compare");
    }

    public interface Dto {

        // @formatter:off
        // Getters

        List<CardMove> getAllCardMovesForTheGame();
        Game getQasinoGame();
        Visitor getQasinoVisitor();
        long getSuppliedGameId();

        // Setters
        void setGameResults(List<Result> results);

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

