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

//    @Mapping(target = "gamesForLeague", source = "league", qualifiedByName = "gamesForLeague")
//    @Mapping(target = "visitor", source = "league", qualifiedByName = "visitor")
    LeagueShortDto toDto(League league);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "ended", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "games", ignore = true)
    League fromDto(LeagueShortDto league);

//    @Named("gamesForLeague")
//    default List<GameShortDto> gamesForLeague(League league) {
//        List<GameShortDto> gameDtos = new ArrayList<>();
//        if (league.getGames() == null) return gameDtos;
//        List<Game> games = league.getGames().stream().toList();
//        if (games.isEmpty()) return gameDtos;
//        for (Game game : games) {
//            gameDtos.add(GameShortMapper.INSTANCE.toDto(game, null));
//        }
//        return gameDtos;
//    }

//    @Named("visitor")
//    default VisitorShortDto visitor(League league) {
//        if (league.getVisitor() == null) {
//            return null;
//        }
//        return VisitorShortMapper.INSTANCE.toDto(league.getVisitor());
//    }

}
