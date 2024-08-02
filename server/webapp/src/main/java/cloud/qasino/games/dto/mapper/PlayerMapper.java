package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.VisitorDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface PlayerMapper {

    // for testing and use in other mappers
    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    @Mapping(target = "cardsInHand", source = "player", qualifiedByName = "cardsInHand")
    @Mapping(target = "visitor", source = "player", qualifiedByName = "visitor")
    PlayerDto toDto(Player player, @Context List<Card> cards);

    List<PlayerDto> toDtoList(List<Player> players, @Context List<Card> cards);

    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "human", ignore = true)
    @Mapping(target = "winner", ignore = true)
    @Mapping(target = "visitor", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "game", ignore = true)
    @Mapping(target = "playing", ignore = true)
    @Mapping(target = "result", ignore = true)
    Player fromDto(PlayerDto player);

    @Mapping(target = "created", ignore = true)
    List<Player> fromDtoList(List<PlayerDto> playerDtos);

    @Named("visitor")
    default VisitorDto visitor(Player player, @Context List<Card> cards) {
        // too much details for a playerDto
        if (player.getVisitor() != null && player.getVisitor().getRoles() != null) player.getVisitor().setRoles(null);
        return VisitorMapper.INSTANCE.toDto(player.getVisitor());
    };

    @Named("cardsInHand")
    default String cardsInHand(Player player, @Context List<Card> cards) {
        // player.getCards does not work!!
        // player.getGame().getCards() does not work!!
        if (cards == null ) return  "[null]";
        if (cards.isEmpty() ) return  "[empty]";
        List<String> handStrings =
                cards.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .filter(hand -> hand.getHand().getPlayerId() == player.getPlayerId())
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }
}
