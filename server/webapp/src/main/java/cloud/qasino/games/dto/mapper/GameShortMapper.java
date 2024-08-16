package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.dto.model.GameShortDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface GameShortMapper {

    // for testing and use in other mappers
    GameShortMapper INSTANCE = Mappers.getMapper(GameShortMapper.class);

//    @Mapping(target = "players", ignore = true)
//    @Mapping(target = "league", ignore = true)
//    @Mapping(target = "cardsInStock", ignore = true)
//    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "gameStateGroup", source = "game", qualifiedByName = "gameStateGroup")
    @Mapping(target = "activePlayerInitiator", source = "game", qualifiedByName = "isActivePlayerInitiator")
    @Mapping(target = "anteToWin", source = "game", qualifiedByName = "anteToWin")
    @Mapping(target = "bettingStrategy", source = "game", qualifiedByName = "bettingStrategy")
    @Mapping(target = "deckConfiguration", source = "game", qualifiedByName = "deckConfiguration")
    @Mapping(target = "oneTimeInsurance", source = "game", qualifiedByName = "oneTimeInsurance")
    @Mapping(target = "roundsToWin", source = "game", qualifiedByName = "roundsToWin")
    @Mapping(target = "turnsToWin", source = "game", qualifiedByName = "turnsToWin")
    GameShortDto toDto(Game game, @Context List<Card> cards);
    List<GameShortDto> toDtoList(List<Game> games, @Context List<Card> cards);

    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "league", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "previousState", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "playing", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Game fromDto(GameShortDto game);

    @Named("gameStateGroup")
    default GameStateGroup gameStateGroup(Game game) {
        return game.getState().getGroup();
    }

    @Named("isActivePlayerInitiator")
    default boolean activePlayerInitiator(Game game) {
        // A game can be in setup still
        if (game.getPlaying() == null) return false;
        return game.getInitiator() == game.getPlaying().getPlayer().getPlayerId();
    }

    @Named("anteToWin")
    default AnteToWin anteToWin(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getAnteToWin();
    }

    @Named("bettingStrategy")
    default BettingStrategy bettingStrategy(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getBettingStrategy();
    }

    @Named("deckConfiguration")
    default DeckConfiguration deckConfiguration(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getDeckConfiguration();
    }

    @Named("oneTimeInsurance")
    default OneTimeInsurance oneTimeInsurance(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getOneTimeInsurance();
    }

    @Named("roundsToWin")
    default RoundsToWin roundsToWin(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getRoundsToWin();
    }

    @Named("turnsToWin")
    default TurnsToWin turnsToWin(Game game) {
        Style style = Style.fromLabelWithDefault(game.getStyle());
        return style.getTurnsToWin();
    }
}
