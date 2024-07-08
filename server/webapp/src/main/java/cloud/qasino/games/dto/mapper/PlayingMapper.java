package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.PlayingDto;
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


    PlayingDto toDto(Playing playing);

    @Mapping(target = "cardMoves", ignore = true)
    Playing fromDto(PlayingDto playing);

    @Named("cardsInHand")
    default String cardsInHand(Playing playing) {
//        List<Card> hand = playing.getCards();
        List<Card> hand = new ArrayList<>();
        List<String> handStrings =
                hand.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }
}
