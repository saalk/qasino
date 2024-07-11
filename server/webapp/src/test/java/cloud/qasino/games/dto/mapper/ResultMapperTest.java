package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ResultMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(playerVisitorResultDto.getResultId(), playerVisitorResult.getResultId());
        // ref
        assertEquals(playerVisitorResultDto.getGame().getType(), playerVisitorResult.getGame().getType());
        assertEquals(playerVisitorResultDto.getGame().getAnte(), playerVisitorResult.getGame().getAnte());
        assertEquals(playerVisitorResultDto.getVisitor().getVisitorId(), playerVisitorResult.getPlayer().getVisitor().getVisitorId());
        // Normal fields
        // derived
//        assertEquals(playerVisitorResultDto.getNextPlayer(), bot);
        // TODO map seats

    }
}