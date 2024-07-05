package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.PlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PlayerMapper {

    @Mapping(target = "cardsInHand", source = "player", qualifiedByName = "cardsInHand")
    PlayerDto toDto(Player player);

    List<PlayerDto> toDtoList(List<Player> players);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "human", ignore = true)
    @Mapping(target = "winner", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    Player fromDto(PlayerDto player);

    @Mapping(target = "created", ignore = true)
    List<Player> fromDtoList(List<PlayerDto> playerDtos);

    @Named("cardsInHand")
    default String cardsInHand(Player player) {
        List<Card> hand = player.getCards();
        List<String> handStrings =
                hand.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }


}
