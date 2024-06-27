package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.dto.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface GameMapper {

    @Mapping(target = "gameStateGroup", source = "game", qualifiedByName = "gameStateGroup")
    @Mapping(target = "activatePlayerInitiator", source = "game", qualifiedByName = "isActivatePlayerInitiator")
    @Mapping(target = "anteToWin", source = "game", qualifiedByName = "anteToWin")
    @Mapping(target = "bettingStrategy", source = "game", qualifiedByName = "bettingStrategy")
    @Mapping(target = "deckConfiguration", source = "game", qualifiedByName = "deckConfiguration")
    @Mapping(target = "oneTimeInsurance", source = "game", qualifiedByName = "oneTimeInsurance")
    @Mapping(target = "roundsToWin", source = "game", qualifiedByName = "roundsToWin")
    @Mapping(target = "turnsToWin", source = "game", qualifiedByName = "turnsToWin")
    GameDto gameToGameDto(Game game);

    @Mapping(target = "state", ignore = true)
    @Mapping(target = "style", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "results", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "week", ignore = true)
    @Mapping(target = "weekday", ignore = true)
    Game gameDtoToGame(GameDto game);

    @Named("isActivatePlayerInitiator")
    default boolean activatePlayerInitiator(Game game) {
        return game.getInitiator() == game.getTurn().getActivePlayer().getPlayerId();
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
    @Named("gameStateGroup")
    default GameStateGroup gameStateGroup(Game game) {
        return game.getState().getGroup();
    }

}
