package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.VisitorDto;
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
    default List<GameDto> gamesForLeague(League league) {
        List<GameDto> gameDtos = new ArrayList<>();
        if (league.getGames() == null) return gameDtos;
        List<Game> games = league.getGames().stream().toList();
        if (games.isEmpty()) return gameDtos;
        for (Game game : games) {
            // to prevent too much details
            game.setLeague(null);
            game.setPlayers(null);
            game.setCards(null);
            game.setResults(null); // do something here later eg calc a total
            game.setPlaying(null);
            gameDtos.add(GameMapper.INSTANCE.toDto(game, null));
        }
        return gameDtos;
    }

    @Named("visitor")
    default VisitorDto visitor(League league) {
        if (league.getVisitor() == null) {
            return null;
        }
        league.getVisitor().setRoles(null);
        return VisitorMapper.INSTANCE.toDto(league.getVisitor());
    }
}
