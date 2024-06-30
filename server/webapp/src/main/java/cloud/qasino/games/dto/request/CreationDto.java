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
    @NotEmpty(message = "Username cannot be empty")
    private String suppliedUsername;
    @Email(message = "Invalid is email")
    private String suppliedEmail;
    @NotEmpty(message = "Password cannot empty")
    private String suppliedPassword;
    @NotEmpty(message = "Alias cannot empty")
    private String suppliedAlias ;

    // league
    @NotEmpty(message = "Name for League cannot be empty")
    private String suppliedLeagueName;

    // player
    @NotEmpty(message = "Player type [eg Bot, Invitee] cannot be empty")
    private PlayerType suppliedPlayerType; // bot, initiator or guest
    @NotNull(message = "Player avatar [eg Elf, Goblin] cannot be empty")
    private Avatar suppliedAvatar;
    @NotNull(message = "Player AiLevel [eg Smart, Human] cannot be empty")
    private AiLevel suppliedAiLevel;

    // game
    @NotNull(message = "Game style [eg MaxRounds, AnteToWin] cannot be empty")
    private String suppliedStyle;
    @NotNull(message = "No Game type choses [eg Highlow, Blackjack]")
    private Type suppliedType;
    @Min(value = 5, message = "min 5 ante")
    private int suppliedAnte = 20;
    @Max(value = 3, message = "max 3 jokers")
    private int suppliedJokers = 3;

    // cardMove
    private List<Card> suppliedCards;
    private Location suppliedLocation;
    private int suppliedBet;

}