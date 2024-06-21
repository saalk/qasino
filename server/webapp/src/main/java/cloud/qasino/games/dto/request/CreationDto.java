package cloud.qasino.games.dto.request;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//@Data
@Getter
@Setter
@Slf4j
public class CreationDto {

    // @formatter:off

    // paging
    @Max(value = 5, message = "max 5 pages")
    private int suppliedPage = 1;
    @Max(value = 5, message = "max 5 rows")
    private int suppliedMaxPerPage = 4;

    // visitor
    @NotEmpty(message = "Username is empty")
    private String suppliedUsername;
    @Email(message = "Invalid is email")
    private String suppliedEmail;
    @NotEmpty(message = "Password is empty")
    private String suppliedPassword;
    @NotEmpty(message = "Alias is empty")
    private String suppliedAlias ;

    // league
    @NotEmpty(message = "Name for League is empty")
    private String suppliedLeagueName;

    // player
    @NotEmpty(message = "Player type eg Bot, Invitee is empty")
    private PlayerType suppliedPlayerType; // bot, initiator or guest
    @NotNull(message = "Player avatar eg Elf, Goblin is empty")
    private Avatar suppliedAvatar;
    @NotNull(message = "Player ai level eg Smart, Human is empty")
    private AiLevel suppliedAiLevel;

    // game
    private String suppliedStyle;
    private Type suppliedType;
    @Min(value = 5, message = "min 5 ante")
    private int suppliedAnte;
    @Max(value = 3, message = "max 3 jokers")
    private int suppliedJokers;

    // cardMove
    private List<Card> suppliedCards;
    private Location suppliedLocation;
    private int suppliedBet;

}