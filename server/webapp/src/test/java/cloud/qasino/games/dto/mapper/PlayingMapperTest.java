package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class PlayingMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(playingDto.getPlayingId(), playing.getPlayingId());
        // ref
//        assertEquals(playingDto.getGame().getType(), playing.getGame().getType());
//        assertEquals(playingDto.getGame().getAnte(), playing.getGame().getAnte());
//        assertEquals(playingDto.getPlayer().getVisitor().getVisitorId(), playing.getPlayer().getVisitor().getVisitorId());
//        assertEquals(playingDto.getPlayer().getSeat(), playing.getPlayer().getSeat());
//        assertEquals(playingDto.getCardMoves(), playing.getCardMoves());
        // Normal fields
        assertEquals(playingDto.getCurrentRoundNumber(), playing.getCurrentRoundNumber());
        assertEquals(playingDto.getCurrentSeatNumber(), playing.getCurrentSeatNumber());
        assertEquals(playingDto.getCurrentMoveNumber(), playing.getCurrentMoveNumber());
        // derived
//        assertEquals(playingDto.getNextPlayer(), bot);
        // TODO map seats

    }
}