package cloud.qasino.games.dto.request;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.dto.validation.GameBasic;
import cloud.qasino.games.dto.validation.LeagueBasic;
import cloud.qasino.games.dto.validation.PlayerBasic;
import cloud.qasino.games.dto.validation.QasinoBasic;
import cloud.qasino.games.dto.validation.VisitorBasic;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreationDto {

    // @formatter:off

    // paging
    @Max(value = 5, message = "max 5 pages", groups = QasinoBasic.class)
    private int suppliedPage = 1;
    @Max(value = 5, message = "max 5 rows", groups = QasinoBasic.class)
    private int suppliedMaxPerPage = 4;

    // visitor
    @NotBlank(message = "Username missing", groups = VisitorBasic.class)
    private String suppliedUsername = "username";
    @Email(message = "Email invalid", groups = VisitorBasic.class)
    private String suppliedEmail = "email@acme.com";
    @Size(min = 3, max = 15, message = "Password invalid 3-15 length", groups = VisitorBasic.class)
    @NotBlank(message = "Password missing", groups = VisitorBasic.class)
    private String suppliedPassword = "secret";
    @NotBlank(message = "Alias missing", groups = VisitorBasic.class)
    private String suppliedAlias = "alias";
    private boolean requestingToRepay = false;
    private boolean offeringShipForPawn = false;

    // league
    @NotBlank(message = "Name for League missing", groups = LeagueBasic.class)
    private String suppliedLeagueName = "league name";

    // player
    @NotBlank(message = "Player type [eg Bot, Invitee] missing", groups = PlayerBasic.class)
    private PlayerType suppliedPlayerType = PlayerType.BOT; // bot, initiator or guest
    @Min(value = 10, message = "10 is the minimum bet ", groups = GameBasic.class)
    @Max(value = 100, message = "100 is the maximum bet", groups = GameBasic.class)
    private int suppliedFiches = 20;
    @NotNull(message = "Player avatar [eg Elf, Goblin] missing", groups = PlayerBasic.class)
    private Avatar suppliedAvatar = Avatar.ELF;
    @NotNull(message = "Player AiLevel [eg Smart, Human] missing", groups = PlayerBasic.class)
    private AiLevel suppliedAiLevel = AiLevel.AVERAGE;

    // game
    @NotNull(message = "Game style [eg MaxRounds, AnteToWin] missing", groups = GameBasic.class)
    private String suppliedStyle = "nrrn22";
    @NotNull(message = "Choose a type of game [eg Highlow, Blackjack]", groups = GameBasic.class)
    private Type suppliedType = Type.HIGHLOW;
    @Min(value = 5, message = "Minimum ante is 5", groups = GameBasic.class)
    @Max(value = 200, message = "Maximum ante is 200", groups = GameBasic.class)
    private int suppliedAnte = 20;
    @Min(value = 0, message = "No jokers is the minimum", groups = GameBasic.class)
    @Max(value = 3, message = "3 jokers is the maximum", groups = GameBasic.class)
    private int suppliedJokers = 3;
    private AnteToWin suppliedAnteToWin; // maxrounds etc
    private BettingStrategy suppliedBettingStrategy; // maxrounds etc
    private DeckConfiguration suppliedDeckConfiguration; // maxrounds etc
    private OneTimeInsurance suppliedOneTimeInsurance; // maxrounds etc
    private RoundsToWin suppliedRoundsToWin; // maxrounds etc
    private TurnsToWin suppliedTurnsToWin; // maxrounds etc

    // cardMove
    @NotBlank(message = "card to move missing", groups = QasinoBasic.class)
    private String suppliedRankAndSuitList = "JR";
    @NotNull(message = "new card location missing", groups = QasinoBasic.class)
    private Location suppliedLocation = Location.HAND;
    @Min(value = 10, message = "min bet is 10", groups = QasinoBasic.class)
    @Max(value = 100, message = "max 100", groups = QasinoBasic.class)
    private int suppliedBet = 20;

}