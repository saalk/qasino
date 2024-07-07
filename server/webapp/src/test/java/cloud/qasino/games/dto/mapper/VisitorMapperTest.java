package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.security.MyUserPrincipal;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.VisitorDto;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VisitorMapperTest {

    @Test
    void givenVisitor_whenMaps_thenProducesCorrectDto() {

        // given a visitor matching MyUserPrincipal and role USER
        Visitor visitor = new Visitor.Builder()
                .withAlias("alias")
                .withAliasSequence(789)
                .withEmail("email@acme.com")
                .withPassword("password")
                .withRoles(Collections.singleton(new Role("ROLE_USER")))
                .withUsername("username")
                .build();
        visitor.pawnShip(Visitor.pawnShipValue(0));
        MyUserPrincipal principal = new MyUserPrincipal(visitor);
        List<Role> visitorRoles = visitor.getRoles().stream().toList();

        // when calling the mapper
        VisitorDto visitorDto = VisitorMapper.INSTANCE.toDto(visitor);

        // then a new VisitorDto is created, based on the given visitor with role ROLE_USER
        // core
        assertEquals(visitorDto.getVisitorId(), visitor.getVisitorId());
        // ref
        assertEquals(visitorDto.getRolesList(), visitorRoles);
        // derived
        assertFalse(visitorDto.isAdmin());
        assertTrue(visitorDto.isUser());
        assertEquals(visitorDto.isRepayPossible(),
                visitor.getBalance() >= visitor.getSecuredLoan());
        // Normal fields
        // password is not mapped
        assertEquals(visitorDto.getUsername(), visitor.getUsername());
        assertEquals(principal.getPassword(), visitor.getPassword());
        assertNull(visitorDto.getPassword());
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