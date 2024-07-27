package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface LeagueMapper {

    // for testing and use in other mappers
    LeagueMapper INSTANCE = Mappers.getMapper(LeagueMapper.class);

    @Mapping(target = "gamesForLeague", source = "league", qualifiedByName = "gamesForLeague")
//    @Mapping(target = "visitor", source = "league", qualifiedByName = "visitor")
    LeagueDto toDto(League league);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "ended", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    League fromDto(LeagueDto league);

    @Named("gamesForLeague")
    default List<GameDto> gamesForLeague(League league) {
        List<GameDto> games = new ArrayList<>();
        if (league.getGames() == null) return games;
        for (Game game : league.getGames()) {
            games.add(GameMapper.INSTANCE.toDto(game, null));
        }
        return games;
    }
    @Named("visitor")
    default VisitorDto visitor(League league) {
        return VisitorMapper.INSTANCE.toDto(league.getVisitor());
    }
}
