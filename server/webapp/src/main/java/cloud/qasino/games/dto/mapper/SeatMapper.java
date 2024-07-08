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


    @Mapping(target = "playerId", source = "player.playerId")
    @Mapping(target = "cardsInHand", source = "player", qualifiedByName = "cardsInHand")
    @Mapping(target = "lastCardInHand", source = "player", qualifiedByName = "lastCardInHand")
    @Mapping(target = "seatPlaying", source = "player", qualifiedByName = "isSeatPlaying")
    @Mapping(target = "seatPlayerTheInitiator", source = "player", qualifiedByName = "isSeatPlayerTheInitiator")
    @Mapping(target = "seatCurrentBet", source = "playing", qualifiedByName = "seatCurrentBet")
    @Mapping(target = "visitorId", source = "player", qualifiedByName = "visitorId")
    @Mapping(target = "username", source = "player", qualifiedByName = "username")
    @Mapping(target = "seatStartBalance", source = "player", qualifiedByName = "seatStartBalance")
    SeatDto toDto(Player player, @Context Playing playing);

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

    @Named("lastCardInHand")
    default Card lastCardInHand(Player player) {
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
    default int seatCurrentBet(Playing playing) {
        return playing.getGame().getAnte();
    }

    @Named("visitorId")
    default long visitorId(Player player) {
        return player.getVisitor().getVisitorId();
    }

    @Named("username")
    default String username(Player player) {
        return player.getVisitor().getUsername();
    }

    @Named("seatStartBalance")
    default int seatStartBalance(Player player) {
        return player.getVisitor().getBalance();
    }
}
