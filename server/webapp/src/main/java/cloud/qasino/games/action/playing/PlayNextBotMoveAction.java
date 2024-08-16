package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cloud.qasino.games.database.service.PlayingService.isRoundEqualToRoundsToWin;

@Slf4j
@Component
public class PlayNextBotMoveAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Autowired PlayingService playingService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!qasino.getGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNextBotMoveAction", "error [" + qasino.getGame().getType() + "]");
        }

        // Next move in HIGHLOW = a given move from location STOCK to location HAND with face UP
        Move nextMove = null;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
//        Player nextPlayer = gameServiceOld.findNextPlayerForGame(game);
        int totalSeats = qasino.getGame().getPlayers().size();
        PlayingDto playing = qasino.getPlaying();
        int currentSeat = playing.getCurrentSeatNumber();
        int currentRound = playing.getCurrentRoundNumber();
        PlayerDto currentPlayer = playing.getCurrentPlayer();

        nextMove = NextMoveCalculator.next(qasino.getGame(), currentPlayer, playing);
        switch (nextMove) {
            case PASS, NEXT -> {
                qasino.getParams().setSuppliedPlayEvent(PlayEvent.PASS);
            }
            default -> {
                qasino.getParams().setSuppliedPlayEvent(PlayEvent.BOT);
            }
        }

        log.info("PlayNextBotMoveAction StrategyMove {}", nextMove);
        // TODO DetermineNextRoundOrEndGame -> move to separate action

        if (nextMove == Move.PASS) {
            if (totalSeats == currentSeat) {
                if (isRoundEqualToRoundsToWin(Style.fromLabelWithDefault(qasino.getGame().getStyle()), currentRound)) {
                    qasino.getParams().setSuppliedPlayEvent(PlayEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                playing.setCurrentRoundNumber(currentRound + 1);
            }
        }

        // Update PLAYING - could be new to new Player
        // TODO what if PASS then deal to next player !!
        PlayingDto newPlaying = playingService.updatePlaying(nextMove, playing.getPlayingId(), playing.getNextPlayer().getPlayerId());
        qasino.setPlaying(newPlaying);

        // Deal CARDs (and update CARDMOVE)
        playingService.dealCardsToPlayer(
                newPlaying,
                qasino.getGame(),
                nextMove,
                fromLocation,
                toLocation,
                face,
                howMany);
        return EventOutput.Result.SUCCESS;
    }
}
