package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlayFirstMoveAction implements Action<PlayFirstMoveAction.Dto, EventOutput.Result> {

    @Autowired
    PlayingService playingService;
    @Autowired
    private PlayingRepository playingRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNext", "error [" + actionDto.getQasinoGame().getType() + "]");
        }

        // First 1 move in HIGHLOW is move DEAL from location STOCK to location HAND with face UP
        Move firstDeal = Move.DEAL;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
        Game game = actionDto.getQasinoGame();
        Player firstPlayer = StreamUtil.findFirstPlayerBySeat(game.getPlayers());

        // new PLAYING
        Playing firstPlaying = new Playing(game, firstPlayer);
        Playing savedPlaying = playingRepository.saveAndFlush(firstPlaying);
        game.setPlaying(savedPlaying);

        // Deal CARDs (and update CARDMOVE)
        playingService.dealCardsToPlayer(
                game,
                firstDeal,
                fromLocation,
                toLocation,
                face,
                howMany);
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        Game getQasinoGame();
    }
}
