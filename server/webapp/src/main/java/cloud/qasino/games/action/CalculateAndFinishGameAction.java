package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.util.ActionUtils;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class CalculateAndFinishGameAction implements Action<CalculateAndFinishGameAction.Dto, EventOutput.Result> {

    @Resource
    TurnAndCardMoveService turnAndCardMoveService;
    @Resource
    CardRepository cardRepository;
    @Resource
    ResultsRepository resultsRepository;
    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(CalculateAndFinishGameAction.Dto actionDto) {

        List<Result> results = resultsRepository.findByGame(actionDto.getQasinoGame());

        if (!(results.isEmpty())) {
            actionDto.setGameResults(results);
            return EventOutput.Result.SUCCESS;
        } else if (actionDto.getQasinoGame() == null) {
            actionDto.setGameResults(null);
            return EventOutput.Result.SUCCESS;
        } else if (actionDto.getQasinoGame().getState().getGroup() != GameStateGroup.FINISHED) {
            actionDto.setGameResults(null);
            return EventOutput.Result.SUCCESS;
        }

        // make a players profit list
        HashMap<Long, Integer> playersProfit = new HashMap<>();
        List<Player> players = actionDto.getQasinoGame().getPlayers();
        for (Player player : players) {
            playersProfit.put(player.getPlayerId(), 0);
        }

        // get all the card moves sorted
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.findCardMovesForGame(actionDto.getQasinoGame()));
        if (actionDto.getAllCardMovesForTheGame() == null || players.isEmpty()) {
            // some error happened - just stop calculating
            return EventOutput.Result.SUCCESS;
        }

        // calculate all players profits - loop cardMoves per player
        for (Player player : players) {
            // iterate per player - should be same as end - begin
            for (CardMove cardMove : actionDto.getAllCardMovesForTheGame()) {
                if (cardMove.getPlayerId() != player.getPlayerId()) {
                    continue; // go to next move
                }
                int currentProfit = playersProfit.get(cardMove.getPlayerId());
                int addProfit = currentProfit + (cardMove.getEndFiches() - cardMove.getStartFiches());
                playersProfit.replace(cardMove.getPlayerId(), addProfit);
            }
        }

        // rank playersProfit by highest desc and create results
        HashMap<Long, Integer> playerProfitSortedOnValue = ActionUtils.sortByValue(playersProfit);

        Player winner = null;
        for (Map.Entry<Long, Integer> en : playerProfitSortedOnValue.entrySet()) {

            Player player = ActionUtils.findPlayerByPlayerId(players, en.getKey());
            boolean won = false;
            // first player has highest profit and thus wins !!
            if (winner == null) {
                winner = player;
                won = true;
            }
            // en.getKey()
            // en.getValue()
            Optional<Visitor> initiator = visitorRepository.findVisitorByVisitorId(actionDto.getQasinoGame().getInitiator());
            Visitor initiatorFound = initiator.isPresent() ? initiator.get() : null;
            resultsRepository.save(new Result(
                    player,
                    initiatorFound,
                    actionDto.getQasinoGame(),
                    actionDto.getQasinoGame().getType(),
                    en.getValue(),
                    won
            ));
        }
        actionDto.setGameResults(resultsRepository.findByGame(actionDto.getQasinoGame()));
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        // @formatter:off
        // Getters
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        Game getQasinoGame();
        Visitor getQasinoVisitor();
        long getSuppliedGameId();
        List<CardMove> getAllCardMovesForTheGame();

        // Setters
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);
        void setGameResults(List<Result> results);

        // error setters
        // @formatter:off
//        void setBadRequestErrorMessage(String problem);
//        void setNotFoundErrorMessage(String problem);
//        void setConflictErrorMessage(String reason);
//        void setUnprocessableErrorMessage(String reason);
//        void setErrorKey(String errorKey);
//        void setErrorValue(String errorValue);
        // @formatter:on
    }
}

