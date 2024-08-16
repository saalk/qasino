package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.Visitor;
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
    @Autowired private ResultsRepository resultsRepository;

    public List<ResultDto> findByGame(ParamsDto params) {
        List<Result> results = resultsRepository.findByGameId(params.getSuppliedGameId());
        return ResultMapper.INSTANCE.toDtoList(results);
    }
    public ResultDto createResult(PlayerDto playerDto, VisitorDto initiatorFound, GameDto gameDto, int fichesWon, boolean isWinner) {
        Player player = PlayerMapper.INSTANCE.fromDto(playerDto);
        Visitor visitor = VisitorMapper.INSTANCE.fromDto(initiatorFound);
        Game game = GameMapper.INSTANCE.fromDto(gameDto);
        Result result = resultsRepository.save(new Result(
                player,
                visitor,
                game,
                game.getType(),
                fichesWon,
                isWinner
        ));
        return ResultMapper.INSTANCE.toDto(result);
    }



}
