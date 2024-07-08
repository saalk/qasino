package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface LeagueMapper {

    // for testing and use in other mappers
    LeagueMapper INSTANCE = Mappers.getMapper(LeagueMapper.class);

    @Mapping(target = "games", source = "league", qualifiedByName = "games")
    @Mapping(target = "visitor", source = "league", qualifiedByName = "visitor")
    LeagueDto toDto(League league);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "ended", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "games", ignore = true)
    League fromDto(LeagueDto league);

    @Named("games")
    default List<GameDto> games(League league) {
        return GameMapper.INSTANCE.toDtoList(league.getGames());
    }
    @Named("visitor")
    default VisitorDto visitor(League league) {
        return VisitorMapper.INSTANCE.toDto(league.getVisitor());
    }
}
