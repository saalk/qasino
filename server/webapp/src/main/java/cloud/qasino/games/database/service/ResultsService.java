package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.ResultDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.ResultMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.request.ParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Lazy
public class ResultsService {

    // @formatter:off
    @Autowired private PlayerRepository playerRepository;
    @Autowired private ResultsRepository resultsRepository;
    @Autowired private GameRepository gameRepository;
    @Autowired private VisitorRepository visitorRepository;

    public List<ResultDto> findByGame(ParamsDto params) {
        List<Result> results = resultsRepository.findByGameId(params.getSuppliedGameId());
        return ResultMapper.INSTANCE.toDtoList(results);
    }
    public ResultDto createResult(ParamsDto params, long playerId, long initiatorId, int fichesWon, boolean isWinner) {
        Game game  = gameRepository.getReferenceById(params.getSuppliedGameId());
        Player player = playerRepository.getReferenceById(playerId);
        Visitor initiator;
        if (playerId > 0) {
            initiator  = visitorRepository.getReferenceById(initiatorId);
        } else {
            initiator  = null;
        }
        Result result = resultsRepository.save(new Result(
                player,
                initiator,
                game,
                game.getType(),
                fichesWon,
                isWinner
        ));
        return ResultMapper.INSTANCE.toDto(result);
    }

}
