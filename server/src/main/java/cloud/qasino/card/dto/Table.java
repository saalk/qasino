package cloud.qasino.card.dto;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.Turn;
import cloud.qasino.card.entity.enums.game.Style;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.move.Move;
import cloud.qasino.card.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Table {

    @JsonProperty("SelectedGame")
    private Game selectedGame;
    @JsonProperty("LastTurn")
    private Turn lastTurn;
    @JsonProperty("PossibleMoves")
    private Move possibleMoves;
    @JsonProperty("Stock")
    private List<Card> stock;
    @JsonProperty("Stats")
    private String totalVsStockCards;
    @JsonProperty("Seats")
    private List<Seat> seats;



}
