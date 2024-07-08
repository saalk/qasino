package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResultMapper {

    // for testing and use in other mappers
    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

    @Mapping(target = "players", source = "result", qualifiedByName = "players")
    @Mapping(target = "visitor", source = "result", qualifiedByName = "visitor")
    @Mapping(target = "game", source = "result", qualifiedByName = "game")
    ResultDto toDto(Result result);

    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    Result fromDto(ResultDto result);

    @Named("players")
    default PlayerDto players(Result result) {
        return PlayerMapper.INSTANCE.toDto(result.getPlayer(), result.getGame().getCards());
    }
    @Named("visitor")
    default VisitorDto visitor(Result result) {
        return VisitorMapper.INSTANCE.toDto(result.getVisitor());
    }
    @Named("game")
    default GameDto game(Result result) {
        return GameMapper.INSTANCE.toDto(result.getGame());
    }
}
