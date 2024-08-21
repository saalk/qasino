package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.pattern.stream.StreamUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlayingMapper {

    // for testing and use in other mappers
    PlayingMapper INSTANCE = Mappers.getMapper(PlayingMapper.class);

//    @Mapping(target = "cardMoves", source = "playing", qualifiedByName = "cardMoves")
    @Mapping(target = "currentPlayer", source = "playing", qualifiedByName = "currentPlayer")
    @Mapping(target = "nextPlayer", source = "playing", qualifiedByName = "nextPlayer")
    @Mapping(target = "seats", ignore = true)
    PlayingDto toDto(Playing playing);

    @Mapping(target = "cardMoves", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "player", ignore = true)
    Playing fromDto(PlayingDto playing);

    @Named("cardMoves")
    default List<CardMove> cardMoves(Playing playing) {
        return playing.getCardMoves();
    }
    @Named("currentPlayer")
    default PlayerDto currentPlayer(Playing playing) {
        return PlayerMapper.INSTANCE.toDto(playing.getPlayer(), playing.getGame().getCards());
    }
    @Named("nextPlayer")
    default PlayerDto nextPlayer(Playing playing) {
        int totalSeats = 0;
        if (playing == null || playing.getGame() == null || playing.getGame().getPlayers() == null) {
            return null;
        } else {
            totalSeats = playing.getGame().getPlayers().size();
        }
        int currentSeat = playing.getCurrentSeatNumber();
        if (totalSeats == 1 || currentSeat == totalSeats) {
            return PlayerMapper.INSTANCE.toDto(playing.getGame().getPlayers().get(0), playing.getGame().getCards());
        }
        List <PlayerDto> unsortedPlayers = PlayerMapper.INSTANCE.toDtoList(playing.getGame().getPlayers(), playing.getGame().getCards());
        List<PlayerDto> sortedPlayers = StreamUtil.sortPlayerDtosOnSeatWithStream(unsortedPlayers);
        return sortedPlayers.get((currentSeat - 1) + 1);
    }
}
