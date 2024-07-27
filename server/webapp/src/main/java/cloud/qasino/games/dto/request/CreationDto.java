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
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
public class CreationDto {

    // @formatter:off

    // paging
    @Max(value = 5, message = "max 5 pages")
    private int suppliedPage = 1;
    @Max(value = 5, message = "max 5 rows")
    private int suppliedMaxPerPage = 4;

    // visitor
    @NotEmpty(message = "Username cannot be empty")
    private String suppliedUsername = "username";
    @Email(message = "Invalid is email")
    private String suppliedEmail = "email";
    @NotEmpty(message = "Password cannot empty")
    private String suppliedPassword = "password";
    @NotEmpty(message = "Alias cannot empty")
    private String suppliedAlias = "alias" ;

    // league
    @NotEmpty(message = "Name for League cannot be empty")
    private String suppliedLeagueName = "league name";

    // player
    @NotEmpty(message = "Player type [eg Bot, Invitee] cannot be empty")
    private PlayerType suppliedPlayerType = PlayerType.BOT; // bot, initiator or guest
    @NotNull(message = "Player avatar [eg Elf, Goblin] cannot be empty")
    private Avatar suppliedAvatar = Avatar.ELF;
    @NotNull(message = "Player AiLevel [eg Smart, Human] cannot be empty")
    private AiLevel suppliedAiLevel = AiLevel.AVERAGE;

    // game
    @NotNull(message = "Game style [eg MaxRounds, AnteToWin] cannot be empty")
    private String suppliedStyle = "nrrn22";
    @NotNull(message = "No Game type choses [eg Highlow, Blackjack]")
    private Type suppliedType = Type.HIGHLOW;
    @Min(value = 5, message = "min 5 ante")
    private int suppliedAnte = 20;
    @Max(value = 3, message = "max 3 jokers")
    private int suppliedJokers = 3;

    // cardMove
    private String suppliedRankAndSuitList = "JR";
    private Location suppliedLocation = Location.HAND;
    private int suppliedBet = 20;

}