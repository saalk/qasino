package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.model.LeagueShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LeagueShortMapper {

    // for testing and use in other mappers
    LeagueShortMapper INSTANCE = Mappers.getMapper(LeagueShortMapper.class);

    LeagueShortDto toDto(League league);

    @Mapping(target = "created", ignore = true)
//    @Mapping(target = "ended", ignore = true)
    League fromDto(LeagueShortDto league);
}
