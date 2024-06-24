package cloud.qasino.games.cardengine.cardplay;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.dto.GameDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Table {

    // SET TABLE
    Type type;
    private int ante;
    GameState gameState;
    GameStateGroup gameStateGroup;
    List<Card> cardsInStockNotInHand;
    private String stringCardsInStockNotInHand;
    public void setTable(GameDto game) {
        if (game == null) {
            return;
        }
        this.type = game.getType();
        this.ante = game.getAnte();
        this.gameState = game.getState();
        this.gameStateGroup = game.getState().getGroup();
        this.cardsInStockNotInHand = null;

        if (game.getTurn() == null) {
            this.activeTurn = null;
            return;
        }
        setActiveTurn(game.getTurn());
    }

    // ACTIVE TURN
    private Turn activeTurn;
    private void setActiveTurn(Turn turn){
        this.activeTurn = turn;
//        this.activePlayer = turn.getActivePlayerId()
    }
    private List<CardMove> allCardMovesForTheGame;

    // ACTIVE PLAYER
    private Player activePlayer;
    private Player nextPlayer;

    // LEAGUE
    String leagueName;
    boolean active;

    // Turn active player
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean human;

    // Turn
    private int currentRoundNumber;
    private int currentSeatNumber;
    private int currentMoveNumber;
    
    private List<Seat> seats;



}
