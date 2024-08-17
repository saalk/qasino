package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.request.ParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
@Slf4j
public class LeaguesService {

    // @formatter:off
    @Autowired private LeagueRepository leagueRepository;

    // lifecycle of a league
    public LeagueDto findOneByLeagueId(ParamsDto paramsDto) {
        League retrievedLeague = leagueRepository.getReferenceById(paramsDto.getSuppliedLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    };
}
