package cloud.qasino.games.action.dto.todo;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
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
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlayFirstMoveAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired PlayingService playingService;
    @Autowired private PlayingRepository playingRepository;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!qasino.getGame().getType().equals(Type.HIGHLOW)) {
            throw new MyNPException("PlayNext", "error [" + qasino.getGame().getType() + "]");
        }

        // First 1 move in HIGHLOW is move DEAL from location STOCK to location HAND with face UP
        Move firstDeal = Move.DEAL;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        // Local fields
        GameDto game = qasino.getGame();
        PlayerDto firstPlayer = StreamUtil.findFirstPlayerBySeat(game.getPlayers());

        // new PLAYING
        PlayingDto firstPlaying = playingService.createPlaying(game.getGameId(), firstPlayer.getPlayerId());
        qasino.setPlaying(firstPlaying);

        // Deal CARDs (and update CARDMOVE)
        playingService.dealCardsToPlayer(
                firstPlaying,
                game,
                firstDeal,
                fromLocation,
                toLocation,
                face,
                howMany);
        return EventOutput.Result.SUCCESS;
    }
}
