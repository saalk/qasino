package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.dto.PlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface PlayerMapper {

    PlayerDto toDto(Player player);

    List<PlayerDto> toDtoList(List<Player> players);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "human", ignore = true)
    @Mapping(target = "winner", ignore = true)
    Player fromDto(PlayerDto player);

    List<Player> fromDtoList(List<PlayerDto> playerDtos);


}
