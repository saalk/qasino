package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class LeagueMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(leagueDto.getLeagueId(), league.getLeagueId());

        // ref
        assertEquals(leagueDto.getVisitor().getAlias(), league.getVisitor().getAlias());
        assertEquals(leagueDto.getGamesForLeague().get(0).getCards(), games.get(0).getCards());

        // normal fields
        assertEquals(leagueDto.getName(), league.getName());
        assertEquals(leagueDto.getNameSequence(), league.getNameSequence());
        assertEquals(leagueDto.isActive(), league.isActive());

        // derived
    }
}