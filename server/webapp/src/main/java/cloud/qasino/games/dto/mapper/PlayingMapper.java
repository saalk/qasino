package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.SeatDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PlayingMapper {

    // for testing and use in other mappers
    PlayingMapper INSTANCE = Mappers.getMapper(PlayingMapper.class);

    @Mapping(target = "game", source = "playing", qualifiedByName = "game")
    @Mapping(target = "player", source = "playing", qualifiedByName = "player")
    @Mapping(target = "cardMoves", source = "playing", qualifiedByName = "cardMoves")
    PlayingDto toDto(Playing playing);

    @Mapping(target = "cardMoves", ignore = true)
    Playing fromDto(PlayingDto playing);

    @Named("game")
    default GameDto game(Playing playing) {
        return GameMapper.INSTANCE.toDto(playing.getGame(), playing.getGame().getCards());
    }

    @Named("player")
    default PlayerDto player(Playing playing) {
        return PlayerMapper.INSTANCE.toDto(playing.getPlayer(), null);
    }

    @Named("cardMoves")
    default List<CardMove> cardMoves(Playing playing) {
        return playing.getCardMoves();
    }


}
