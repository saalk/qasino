package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.HandDto;
import cloud.qasino.games.dto.PlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface HandMapper {

    // for testing and use in other mappers
    HandMapper INSTANCE = Mappers.getMapper(HandMapper.class);

    @Mapping(target = "cards", source = "cardMoves", qualifiedByName = "cards")
    @Mapping(target = "rankSuits", source = "cardMoves", qualifiedByName = "rankSuits")
    @Mapping(target = "roundNumber", source = "round", qualifiedByName = "roundNumber")
    @Mapping(target = "seatNumber", source = "seat", qualifiedByName = "seatNumber")
    @Mapping(target = "cardsInRoundAndSeat", source = "cardMoves", qualifiedByName = "cardsInRoundAndSeat")
    @Mapping(target = "cardsDeltaInRoundAndSeat", source = "cardMoves", qualifiedByName = "cardsDeltaInRoundAndSeat")
    HandDto toDto(List<CardMove> cardMoves, int round, int seat);

    @Named("cards")
    default List<Card> cards(List<CardMove> cardMoves) {
        return null;
    }
    @Named("rankSuits")
    default List<String> rankSuits(List<CardMove> cardMoves) {
        return null;
    }
    @Named("roundNumber")
    default int roundNumber(int round) {
        return round;
    }
    @Named("seatNumber")
    default int seatNumber(int seat) {
        return seat;
    }
    @Named("cardsInRoundAndSeat")
    default String cardsInRoundAndSeat(List<CardMove> cardMoves) {
        return null;
    }
    @Named("cardsDeltaInRoundAndSeat")
    default String cardsDeltaInRoundAndSeat(List<CardMove> cardMoves) {
        return null;
    }

}
