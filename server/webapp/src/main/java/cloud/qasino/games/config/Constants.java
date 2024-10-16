package cloud.qasino.games.config;

import cloud.qasino.games.database.entity.enums.game.style.*;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.card.playingcard.Rank;
import cloud.qasino.games.database.entity.enums.card.playingcard.Suit;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface Constants {

    String BASE_PATH = "/qasino/api";
    String ENDPOINT_VISITOR = "/visitor";
    String ENDPOINT_GAME = "/game";
    String ENDPOINT_PLAYER = "/player";
    String ENDPOINT_CARD = "/card";
    String ENDPOINT_PLAY = "/play";

    String NUMERIC_REGEX = "^([\\p{N}]+)$";
    String ALPHANUMERIC_REGEX = "^([\\p{N}\\p{L}]+)$";
    String UUID_REGEX = "^([\\p{N}\\p{L}\\-]+)$";

    // TODO move to properties
    int DEFAULT_PAWN_SHIP_HUMAN = 1000;
    int DEFAULT_PAWN_SHIP_BOT = 1000;

    // only call after hasNoBooleanValue check
    static boolean getBooleanValue(String bool) {
        // checks true, false, on. off case-insensitive
        return BooleanUtils.toBooleanObject(bool);
    }

    // helper for checking header, path and request params
    static boolean isNullOrEmpty(int value) {
        // str.isEmpty will throw a nullpointer if e == null
        return value <= 0;
    }

    static boolean isNullOrEmpty(String str) {
        // str.isEmpty will throw a nullpointer if e == null
        return str == null || str.trim().isEmpty();
    }

    static boolean isNullOrEmptyAndNoValidInt(String str) {
        // str.isEmpty will throw a nullpointer if e == null
        return isNullOrEmpty(str)
                || !StringUtils.isNumeric(str)
                || Integer.parseInt(str) <= 0;

    }

    static boolean isNullOrEmpty(Integer integer) {
        // str.isEmpty will throw a nullpointer if e == null
        return integer == null || integer <= 0;
    }

    static boolean isNullOrEmpty(Boolean bool) {
        // str.isEmpty will throw a nullpointer if e == null
        return bool == null;
    }

    static boolean isNullOrEmpty(List<?> e) {
        // e.isEmpty will throw a nullpointer if e == null
        return e == null || e.isEmpty();
    }

    static boolean isNullOrEmpty(Object o) {
        // e.isEmpty will throw a nullpointer if e == null
        return o == null;
    }

    static boolean hasNoBooleanValue(String bool) {
        // checks true, false, on. off case-insensitive
        return (BooleanUtils.toBooleanObject(bool) == null);
    }

    static <E> boolean isNoValidEnum(String e) {
        if (isNullOrEmpty(e)) return true;
        return !EnumUtils.isValidEnum(GameState.class, e) &&
                GameState.fromLabelWithDefault(e) == GameState.ERROR &&
                !EnumUtils.isValidEnum(GameEvent.class, e) &&
                !EnumUtils.isValidEnum(BettingStrategy.class, e) &&
                !EnumUtils.isValidEnum(DeckConfiguration.class, e) &&
                !EnumUtils.isValidEnum(OneTimeInsurance.class, e) &&
                !EnumUtils.isValidEnum(AnteToWin.class, e) &&
                !EnumUtils.isValidEnum(RoundsToWin.class, e) &&
                !EnumUtils.isValidEnum(TurnsToWin.class, e) &&
                !EnumUtils.isValidEnum(Move.class, e) &&
                Move.fromLabelWithDefault(e) == Move.ERROR &&
                !EnumUtils.isValidEnum(Type.class, e) &&
                Type.fromLabelWithDefault(e) == Type.ERROR &&
                !EnumUtils.isValidEnum(AiLevel.class, e) &&
                AiLevel.fromLabelWithDefault(e) == AiLevel.ERROR &&
                !EnumUtils.isValidEnum(Avatar.class, e) &&
                Avatar.fromLabelWithDefault(e) == Avatar.ERROR &&
                !EnumUtils.isValidEnum(PlayerType.class, e) &&
                PlayerType.fromLabelWithDefault(e) == PlayerType.ERROR &&
                !EnumUtils.isValidEnum(Face.class, e) &&
                Face.fromLabelWithDefault(e) == Face.ERROR &&
                !EnumUtils.isValidEnum(Location.class, e) &&
                Location.fromLabelWithDefault(e) == Location.ERROR &&
                !EnumUtils.isValidEnum(Position.class, e) &&
                Position.fromLabelWithDefault(e) == Position.ERROR &&
                !EnumUtils.isValidEnum(Rank.class, e) &&
                !EnumUtils.isValidEnum(Suit.class, e); // its actual a valid and meaning full enum
    }
}
