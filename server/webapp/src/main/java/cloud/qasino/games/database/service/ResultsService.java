package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.GameShortDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.GameShortMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.ResultMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
