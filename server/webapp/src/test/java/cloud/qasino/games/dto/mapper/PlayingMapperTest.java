package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import cloud.qasino.games.simulator.QasinoSimulator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class PlayingMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(playingDto.getPlayingId(), playing.getPlayingId());
        // ref
        assertEquals(playingDto.getGame().getType(), playing.getGame().getType());
        assertEquals(playingDto.getGame().getAnte(), playing.getGame().getAnte());
        assertEquals(playingDto.getPlayer().getVisitor().getVisitorId(), playing.getPlayer().getVisitor().getVisitorId());
        assertEquals(playingDto.getPlayer().getSeat(), playing.getPlayer().getSeat());
        assertEquals(playingDto.getCardMoves(), playing.getCardMoves());
        // Normal fields
        assertEquals(playingDto.getCurrentRoundNumber(), playing.getCurrentRoundNumber());
        assertEquals(playingDto.getCurrentSeatNumber(), playing.getCurrentSeatNumber());
        assertEquals(playingDto.getCurrentMoveNumber(), playing.getCurrentMoveNumber());
        // derived
//        assertEquals(playingDto.getNextPlayer(), bot);
        // TODO map seats

    }
}