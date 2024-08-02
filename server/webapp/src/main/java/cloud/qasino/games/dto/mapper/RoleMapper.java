package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.security.Privilege;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.dto.CardDto;
import cloud.qasino.games.dto.RoleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RoleMapper {

    // for testing and use in other mappers
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    @Mapping(target = "privilegeNames", source = "role", qualifiedByName = "privilegeNames")
    RoleDto toDto(Role role);

    List<RoleDto> toDtoList(List<Role> role);

    @Mapping(target = "visitors", ignore = true)
    @Mapping(target = "privileges", ignore = true)
    Role fromDto(RoleDto role);

    @Named("privilegeNames")
    default List<String> privilegeNames(Role role){
        if (role.getPrivileges() != null ) {
            return role.getPrivileges().stream().map(Privilege::getName).toList();
        } else {
            return new ArrayList<>();
        }
    }
}
