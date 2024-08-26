package cloud.qasino.games.action.game;

import cloud.qasino.games.action.common.ActionUtils;
import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.database.service.ResultsService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.ResultDto;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
public class CalculateAndFinishGameAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource ResultsService resultsService;
    @Resource PlayingService playingService;
    @Resource CardRepository cardRepository;
    @Resource ResultsRepository resultsRepository;
    @Resource VisitorRepository visitorRepository;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        List<ResultDto> results = resultsService.findByGame(qasino.getParams());

        if (!(results.isEmpty())) {
            qasino.setResults(results);
            return EventOutput.Result.SUCCESS;
        } else if (qasino.getGame() == null) {
            qasino.setResults(null);
            return EventOutput.Result.SUCCESS;
        } else if (qasino.getPlaying() == null) { // stopped before playing
            qasino.setResults(null);
            return EventOutput.Result.SUCCESS;
        } else if (qasino.getGame().getState().getGroup() != GameStateGroup.FINISHED) { // still playing
            qasino.setResults(null);
            return EventOutput.Result.SUCCESS;
        }

//        log.info("CalculateAndFinishGameAction {}",qasino.getGame());
        // make a players profit list
        HashMap<Long, Integer> playersProfit = new HashMap<>();
        List<PlayerDto> players = qasino.getGame().getPlayers();
        for (PlayerDto player : players) {
            playersProfit.put(player.getPlayerId(), 0);
        }

        // get all the card moves sorted
        List<CardMove> cardMoves = playingService.findCardMovesForGame(qasino.getPlaying());
        if (cardMoves.isEmpty() || players.isEmpty()) {
            // some error happened - just stop calculating
            return EventOutput.Result.SUCCESS;
        }

        // calculate all players profits - loop cardMoves per player
        for (PlayerDto player : players) {
            // iterate per player - should be same as end - begin
            for (CardMove cardMove : cardMoves) {
                if (cardMove.getPlayerId() != player.getPlayerId()) {
                    continue; // go to next move
                }
                int currentProfit = playersProfit.get(cardMove.getPlayerId());
                int addProfit = currentProfit + (cardMove.getEndFiches() - cardMove.getStartFiches());
                playersProfit.replace(cardMove.getPlayerId(), addProfit);
            }
        }

        // rank playersProfit by highest desc and create results
        HashMap<Long, Integer> playerProfitSortedOnValue = ActionUtils.sortByValueDesc(playersProfit);

        boolean won = true;
        for (Map.Entry<Long, Integer> en : playerProfitSortedOnValue.entrySet()) {
            PlayerDto player = ActionUtils.findPlayerByPlayerId(players, en.getKey());
            // first player has highest profit and thus wins !!
            Optional<Visitor> initiator = visitorRepository.findVisitorByVisitorId(qasino.getGame().getInitiator());
            long initiatorFound = initiator.isPresent() ? initiator.get().getVisitorId() : null;
//            log.info("result isWinner {}", (won) ? "true" : "false");
//            log.info("result player {} value {}", player.getPlayerId(), en.getValue() );

            resultsService.createResult(
                    qasino.getParams(),
                    player.getPlayerId(),
                    initiatorFound,
                    en.getValue(),
                    won
            );
            if (won) {
                won = false; // there can be only one
            }

        }
        qasino.setResults(resultsService.findByGame(qasino.getParams()));
        return EventOutput.Result.SUCCESS;
    }
}

