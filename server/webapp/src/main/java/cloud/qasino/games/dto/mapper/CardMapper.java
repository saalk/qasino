package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.dto.model.CardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CardMapper {

    // for testing and use in other mappers
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Mapping(target = "handId", source = "card", qualifiedByName = "handId")
    CardDto toDto(Card card);

    List<CardDto> toDtoList(List<Card> card);

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "hand", ignore = true)
    Card fromDto(CardDto card);

    @Named("handId")
    default long handId(Card card){
        if (card.getHand() != null ) {
            return card.getHand().getPlayerId();
        } else {
            return 0;
        }
    }
}
