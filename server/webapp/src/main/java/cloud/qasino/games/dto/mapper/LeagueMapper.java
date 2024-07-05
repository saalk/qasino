package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface LeagueMapper {

    GameMapper gameMapper = new GameMapperImpl();
    VisitorMapper visitorMapper = new VisitorMapperImpl();

    @Mapping(target = "gameDtos", source = "league", qualifiedByName = "gameDtos")
    @Mapping(target = "visitorDto", source = "league", qualifiedByName = "visitorDto")
    LeagueDto toDto(League league);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "ended", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "games", ignore = true)
    League fromDto(LeagueDto league);

    @Named("gameDtos")
    default List<GameDto> gameDtos(League league) {
        return gameMapper.toDtoList(league.getGames());
    }
    @Named("visitorDto")
    default VisitorDto visitorDto(League league) {
        return visitorMapper.toDto(league.getVisitor());
    }
}
