package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Location;
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

@Mapper(uses = {HandMapper.class})
public interface SeatMapper {

    // for testing and use in other mappers
    SeatMapper INSTANCE = Mappers.getMapper(SeatMapper.class);

    @Mapping(target = "playerId", source = "player", qualifiedByName = "playerId")
    @Mapping(target = "cardsInHand", source = "player", qualifiedByName = "cardsInHand")
    @Mapping(target = "lastCardInHand", source = "player", qualifiedByName = "lastCardInHand")
    @Mapping(target = "seatPlaying", source = "player", qualifiedByName = "isSeatPlaying")
    @Mapping(target = "seatPlayerTheInitiator", source = "player", qualifiedByName = "isSeatPlayerTheInitiator")
    @Mapping(target = "seatCurrentBet", source = "player", qualifiedByName = "seatCurrentBet")
    @Mapping(target = "visitorId", source = "player", qualifiedByName = "visitorId")
    @Mapping(target = "username", source = "player", qualifiedByName = "username")
    @Mapping(target = "seatStartBalance", source = "player", qualifiedByName = "seatStartBalance")
    SeatDto toDto(Player player, @Context Playing playing);

    @Named("playerId")
    default long playerId(Player player, @Context Playing playing) {
        return player.getPlayerId();
    }

    @Named("cardsInHand")
    default String cardsInHand(Player player, @Context Playing playing) {
        // player.getCards does not work!!
        // player.getGame().getCards() does not work!!
        List<Card> cards = playing.getGame().getCards();
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

    @Named("lastCardInHand")
    default Card lastCardInHand(Player player, @Context Playing playing) {
        return null;
    }

    @Named("isSeatPlaying")
    default boolean seatPlaying(Player player, @Context Playing playing) {
        return player.getPlayerId() == playing.getPlayer().getPlayerId();
    }

    @Named("isSeatPlayerTheInitiator")
    default boolean seatPlayerTheInitiator(Player player, @Context Playing playing) {
        return player.getPlayerId() == playing.getGame().getInitiator();
    }

    @Named("seatCurrentBet")
    default int seatCurrentBet(Player player, @Context Playing playing) {
        return playing.getGame().getAnte();
    }

    @Named("visitorId")
    default long visitorId(Player player, @Context Playing playing) {
        if (player.getVisitor() == null ) return 0; // bots are no visitor
        return player.getVisitor().getVisitorId();
    }

    @Named("username")
    default String username(Player player, @Context Playing playing) {
        if (player.getVisitor() == null ) return ""; // bots are no visitor
        return player.getVisitor().getUsername();
    }

    @Named("seatStartBalance")
    default int seatStartBalance(Player player, @Context Playing playing) {
        if (player.getVisitor() == null ) return 0; // bots are no visitor
        return player.getVisitor().getBalance();
    }
}
