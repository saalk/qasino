package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.dto.ResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ResultMapper {

    ResultDto resultToResultDto(Result result);

    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "player", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "type", ignore = true)

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    Result resultDtoToResult(ResultDto result);

}
