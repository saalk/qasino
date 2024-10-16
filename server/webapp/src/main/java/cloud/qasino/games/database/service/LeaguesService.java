package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.request.ParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Lazy
@Slf4j
public class LeaguesService {

    // @formatter:off
    @Autowired private LeagueRepository leagueRepository;

    // lifecycle of a league - aim to pass params and creation dto's for consistency for all services
    public LeagueDto findOneByLeagueId(ParamsDto params) {
        League retrievedLeague = leagueRepository.getReferenceById(params.getSuppliedLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    }
    public List<LeagueDto> findLeaguesForVisitorWithPage(ParamsDto params) {
        Pageable pageable = PageRequest.of(0, 4);
        List<League> leagues = leagueRepository.findLeaguesForVisitorWithPage(params.getSuppliedVisitorId(), pageable);
        return LeagueMapper.INSTANCE.toDtoList(leagues);
    }
    public LeagueDto findlatestLeagueForVisitor(ParamsDto params) {
        List<LeagueDto> leagues = findLeaguesForVisitorWithPage(params);
        return (leagues.isEmpty()) ? null : leagues.get(0);
    }
    public LeagueDto saveNewLeague(League league) {
        League savedLeague = leagueRepository.save(league);
        League retrievedLeague = leagueRepository.getReferenceById(savedLeague.getLeagueId());
        return LeagueMapper.INSTANCE.toDto(retrievedLeague);
    }
    public Long countByName(String name) {
        return leagueRepository.countByName(name);
    }
}
