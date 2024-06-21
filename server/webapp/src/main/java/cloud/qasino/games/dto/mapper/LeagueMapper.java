package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.dto.LeagueDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LeagueMapper {

    LeagueDto leagueToLeagueDto(League league);

    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "games", ignore = true)
    League leagueDtoToLeague(LeagueDto league);

}
