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

@Mapper
public interface ResultMapper {

    PlayerMapper playerMapper = new PlayerMapperImpl();
    GameMapper gameMapper = new GameMapperImpl();
    VisitorMapper visitorMapper = new VisitorMapperImpl();


    @Mapping(target = "playerDto", source = "result", qualifiedByName = "playerDto")
    @Mapping(target = "visitorDto", source = "result", qualifiedByName = "visitorDto")
    @Mapping(target = "gameDto", source = "result", qualifiedByName = "gameDto")
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

    @Named("playerDto")
    default PlayerDto playerDto(Result result) {
        return playerMapper.toDto(result.getPlayer());
    }
    @Named("visitorDto")
    default VisitorDto visitorDto(Result result) {
        return visitorMapper.toDto(result.getVisitor());
    }
    @Named("gameDto")
    default GameDto gameDto(Result result) {
        return gameMapper.toDto(result.getGame());
    }
}
