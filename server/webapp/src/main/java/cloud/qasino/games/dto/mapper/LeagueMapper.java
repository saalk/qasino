package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.model.GameShortDto;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.VisitorShortDto;
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
    @Mapping(target = "visitor", source = "league", qualifiedByName = "visitor")
    LeagueDto toDto(League league);
    List<LeagueDto> toDtoList(List<League> league);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "ended", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "games", ignore = true)
    League fromDto(LeagueDto league);

    @Named("gamesForLeague")
    default List<GameShortDto> gamesForLeague(League league) {
        List<GameShortDto> gameDtos = new ArrayList<>();
        if (league.getGames() == null) return gameDtos;
        List<Game> games = league.getGames().stream().toList();
        if (games.isEmpty()) return gameDtos;
        for (Game game : games) {
            gameDtos.add(GameShortMapper.INSTANCE.toDto(game, null));
        }
        return gameDtos;
    }

    @Named("visitor")
    default VisitorShortDto visitor(League league) {
        if (league.getVisitor() == null) {
            return null;
        }
        return VisitorShortMapper.INSTANCE.toDto(league.getVisitor());
    }
}
