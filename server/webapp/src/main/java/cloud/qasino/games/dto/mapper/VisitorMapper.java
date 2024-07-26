package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface VisitorMapper {

    // for testing and use in other mappers
    VisitorMapper INSTANCE = Mappers.getMapper(VisitorMapper.class);

    @Mapping(target = "password", ignore = true)
//    @Mapping(target = "initiatedGamesForVisitor", ignore = true)
//    @Mapping(target = "invitedGamesForVisitor", ignore = true)
    @Mapping(target = "admin", source = "visitor", qualifiedByName = "isTheAdmin")
    @Mapping(target = "user", source = "visitor", qualifiedByName = "isTheUser")
    @Mapping(target = "repayPossible", source = "visitor", qualifiedByName = "canRepay")
    @Mapping(target = "rolesList", source = "visitor", qualifiedByName = "rolesList")
    VisitorDto toDto(Visitor visitor);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "using2FA", ignore = true)
    @Mapping(target = "aliasSequence", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "securedLoan", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "leagues", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Visitor fromDto(VisitorDto visitor);

    @Named("canRepay")
    default boolean canRepay(Visitor visitor){
        return visitor.getBalance() >= visitor.getSecuredLoan();
    }
    @Named("isTheAdmin")
    default boolean isTheAdmin(Visitor visitor){
        return false;
    }
    @Named("isTheUser")
    default boolean isTheUser(Visitor visitor){
        return true;
    }
    @Named("rolesList")
    default List<Role> roles(Visitor visitor){
        return visitor.getRoles().stream().toList();
    }
}
