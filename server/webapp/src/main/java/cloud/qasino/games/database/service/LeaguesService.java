package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.request.ParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Lazy
@Slf4j
public class LeaguesService {

    // @formatter:off
    @Autowired private LeagueRepository leagueRepository;

    // lifecycle of a visitor - aim to pass params and creation dto's for consistency for all services
    public LeagueDto findOneByLeagueId(ParamsDto paramsDto) {
        League retrievedLeague = leagueRepository.getReferenceById(paramsDto.getSuppliedLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    };
    public LeagueDto saveNewLeague(League league) {
        League savedLeague = leagueRepository.save(league);
        League retrievedLeague = leagueRepository.getReferenceById(savedLeague.getLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    }

    public Long countByName(String name) {
        return leagueRepository.countByName(name);
    };

}
