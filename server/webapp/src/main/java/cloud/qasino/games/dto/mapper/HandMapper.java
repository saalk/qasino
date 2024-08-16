package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.dto.model.HandDto;
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

//    @Mapping(target = "rankSuitsList", source = "cardMoves", qualifiedByName = "rankSuitsList")
    @Mapping(target = "rankSuits", source = "cardMoves", qualifiedByName = "rankSuits")
    @Mapping(target = "roundNumber", source = "round", qualifiedByName = "roundNumber")
    @Mapping(target = "seatNumber", source = "seat", qualifiedByName = "seatNumber")
    @Mapping(target = "cardsInRoundAndSeat", source = "cardMoves", qualifiedByName = "cardsInRoundAndSeat")
    @Mapping(target = "cardsDeltaInRoundAndSeat", source = "cardMoves", qualifiedByName = "cardsDeltaInRoundAndSeat")
    HandDto toDto(List<CardMove> cardMoves, int round, int seat);

    @Named("rankSuitsList")
    default List<String> rankSuitsList(List<CardMove> cardMoves) {
        return cardMoves.stream()
                .map(CardMove::getCardMoveDetails)
                .collect(Collectors.toList());
    }
    @Named("rankSuits")
    default String rankSuits(List<CardMove> cardMoves) {
        List<String> handStrings = cardMoves.stream()
                .map(CardMove::getCardMoveDetails)
                .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
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
