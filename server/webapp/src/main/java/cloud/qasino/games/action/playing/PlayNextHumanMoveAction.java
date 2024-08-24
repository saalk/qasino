package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cloud.qasino.games.database.service.PlayingService.isRoundEqualToRoundsToWin;
import static cloud.qasino.games.database.service.PlayingService.mapPlayEventToMove;

@Slf4j
@Component
public class PlayNextHumanMoveAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Autowired PlayingService playingService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!qasino.getGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNextHumanMoveAction", "error [" + qasino.getGame().getType() + "]");
        }

        // Next move in HIGHLOW = a given move from location STOCK to location HAND with face UP
        Move nextMove = mapPlayEventToMove(qasino.getParams().getSuppliedPlayEvent());
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
        PlayerDto nextPlayer = qasino.getPlaying().getNextPlayer();
        int totalSeats = qasino.getPlaying().getSeats().size();
        if (qasino.getGame().getPlayers().isEmpty()) {
            throw new MyNPException("PlayNextHumanMoveAction", "error [" + qasino.getGame()+ "]");
        }
        PlayingDto playing = qasino.getPlaying();
        int currentSeat = playing.getCurrentSeatNumber();
        int currentRound = playing.getCurrentRoundNumber();

        // TODO DetermineNextRoundOrEndGame -> move to separate action
        if (qasino.getParams().getSuppliedPlayEvent() == PlayEvent.PASS) {
            if (totalSeats == currentSeat) { // last seat
                if (isRoundEqualToRoundsToWin(Style.fromLabelWithDefault(qasino.getGame().getStyle()), currentRound)) {
                    qasino.getParams().setSuppliedPlayEvent(PlayEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                playing.setCurrentRoundNumber(currentRound + 1);
            }
        }

        // Update PLAYING - could be new to new Player
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
