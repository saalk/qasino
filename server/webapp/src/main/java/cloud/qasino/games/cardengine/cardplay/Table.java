package cloud.qasino.games.cardengine.cardplay;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.SeatDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Table {

    private List<SeatDto> seatDtos;

    // FROM GAME - LEAGUE
    String leagueName;
    boolean active;

    // FROM GAME
    Type type;
    private int ante;
    GameState gameState;
    GameStateGroup gameStateGroup;
    List<Card> cardsInTheGameSorted;
    List<Card> cardsInStockNotInHandSorted;
    private String stringCardsInStockNotInHand;
    private String countStockAndTotal;

    // FROM PLAYING
    private List<CardMove> allCardMovesForTheGame;
    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;

    // FROM PLAYING - ACTIVE AND NEXT PLAYER
    private PlayerDto player;
    private PlayerDto nextPlayer;
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean human;

}
