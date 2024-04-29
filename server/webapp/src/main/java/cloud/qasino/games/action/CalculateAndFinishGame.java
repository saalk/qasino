package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.util.ActionUtils;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class CalculateAndFinishGame implements Action<CalculateAndFinishGame.Dto, EventOutput.Result> {

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
    public EventOutput.Result perform(CalculateAndFinishGame.Dto actionDto) {

        HashMap<Long, Integer> playerProfit = new HashMap<>();
        List<Player> players = actionDto.getQasinoGame().getPlayers();
        Optional<Visitor> initiator = visitorRepository.findVisitorByVisitorId(actionDto.getQasinoGame().getInitiator());

        for (Player player : players) {
            playerProfit.put(player.getPlayerId(), 0);
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
        return EventOutput.Result.SUCCESS;
    }

    void setErrorMessageConflict(CalculateAndFinishGame.Dto actionDto, String id,
                                 String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid, no previous card to compare");

    }

    private void setErrorMessageBadRequest(CalculateAndFinishGame.Dto actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }

    public interface Dto {

        // @formatter:off
        // Getters

        List<CardMove> getAllCardMovesForTheGame();
        Game getQasinoGame();
        Visitor getQasinoVisitor();

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}

