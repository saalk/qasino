package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisitorMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(visitorDto.getVisitorId(), visitor.getVisitorId());
        // ref
        assertEquals(visitorDto.getRolesList(), visitorRoles);
        if (visitor.getRoles() != null) {
            assertEquals(visitorDto.getRoles(), Arrays.toString(visitor.getRoles().stream().map(Role::getName).toArray()));
        }
        // derived
        assertFalse(visitorDto.isAdmin());
        assertTrue(visitorDto.isUser());
        assertEquals(visitorDto.isRepayPossible(), visitor.getBalance() >= visitor.getSecuredLoan());
        // normal fields
        assertEquals(visitorDto.getUsername(), visitor.getUsername());
        assertEquals(principal.getPassword(), visitor.getPassword());
//        assertNull(visitorDto.getPassword()); // ignore
        assertEquals(visitorDto.getAlias(), visitor.getAlias());
        assertEquals(visitorDto.getAliasSequence(), visitor.getAliasSequence());
        assertEquals(visitorDto.getEmail(), visitor.getEmail());
        assertEquals(visitorDto.getBalance(), visitor.getBalance());
        assertEquals(visitorDto.getSecuredLoan(), visitor.getSecuredLoan());
        assertEquals(visitorDto.getYear(), visitor.getYear());
        assertEquals(visitorDto.getMonth(), visitor.getMonth());
        assertEquals(visitorDto.getWeek(), visitor.getWeek());
        assertEquals(visitorDto.getWeekday(), visitor.getWeekday());
    }
}