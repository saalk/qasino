package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.service.GameServiceOld;
import cloud.qasino.games.database.service.PlayingService;
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
public class PlayNextHumanMoveAction implements Action<PlayNextHumanMoveAction.Dto, EventOutput.Result> {

    @Autowired
    PlayingService playingService;
    @Autowired
    PlayingRepository playingRepository;
    @Autowired
    private GameServiceOld gameServiceOld;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNextHumanMoveAction", "error [" + actionDto.getQasinoGame().getType() + "]");
        }

        // Next move in HIGHLOW = a given move from location STOCK to location HAND with face UP
        Move nextMove = mapPlayEventToMove(actionDto.getSuppliedPlayEvent());
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
        Game game = actionDto.getQasinoGame();
        Player nextPlayer = gameServiceOld.findNextPlayerForGame(game);
        int totalSeats = game.getPlayers().size();
        Playing playing = game.getPlaying();
        int currentSeat = playing.getCurrentSeatNumber();
        int currentRound = playing.getCurrentRoundNumber();

        // TODO DetermineNextRoundOrEndGame -> move to separate action
        if (actionDto.getSuppliedPlayEvent() == PlayEvent.PASS) {
            if (totalSeats == currentSeat) {
                if (isRoundEqualToRoundsToWin(Style.fromLabelWithDefault(game.getStyle()), currentRound)) {
                    actionDto.setSuppliedPlayEvent(PlayEvent.END_GAME);
                    return EventOutput.Result.SUCCESS;
                }
                playing.setCurrentRoundNumber(currentRound + 1);
            }
        }

        // Update PLAYING - could be new to new Player
        playingService.updatePlaying(nextMove, playing, nextPlayer);
        Playing newPlaying = playingRepository.save(playing);
        game.setPlaying(newPlaying);

        // Deal CARDs (and update CARDMOVE)
        playingService.dealCardsToPlayer(
                game,
                nextMove,
                fromLocation,
                toLocation,
                face,
                howMany);
        return EventOutput.Result.SUCCESS;
    }
    // @formatter:off
    public interface Dto {
        Game getQasinoGame();
        PlayEvent getSuppliedPlayEvent();
        void setSuppliedPlayEvent(PlayEvent playEvent);
    }
}
