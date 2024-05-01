package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.dto.elements.SectionSeat;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapTableFromRetrievedDataAction implements Action<MapTableFromRetrievedDataAction.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        SectionTable table = new SectionTable();
        if ((actionDto.getActiveTurn() == null)) {
            return EventOutput.Result.SUCCESS;
        }
        table.setCurrentTurn(actionDto.getActiveTurn());
        table.setPossibleMoves(new ArrayList<>());
        switch (actionDto.getQasinoGame().getType()) {
            case HIGHLOW -> {
                table.getPossibleMoves().add(Move.HIGHER);
                table.getPossibleMoves().add(Move.LOWER);
                table.getPossibleMoves().add(Move.PASS);
                table.getPossibleMoves().add(Move.NEXT);
            }
            case BLACKJACK -> {
                table.getPossibleMoves().add(Move.DEAL);
                table.getPossibleMoves().add(Move.DOUBLE);
                table.getPossibleMoves().add(Move.STAND);
            }
        }
        List<Card> stockNotInHand =
                actionDto.getCardsInTheGameSorted()
                        .stream()
                        .filter(c -> c.getHand() == null)
                        .collect(Collectors.toList());
        table.setCardsInStockNotInHand(stockNotInHand);
        table.setStringCardsInStockNotInHand(String.valueOf(stockNotInHand));
        table.setCountCardsInStockNotInHand(stockNotInHand.size());
        table.setSeats(setSeats(actionDto));
        actionDto.setTable(table);
        return EventOutput.Result.SUCCESS;
    }

    private List<SectionSeat> setSeats(Dto actionDto) {

        List<SectionSeat> seats = new ArrayList<>();
        for (Player player : actionDto.getQasinoGamePlayers()) {
            SectionSeat seat = new SectionSeat();
            // seat stats
            seat.setSeatId(player.getSeat());
            seat.setPlaying(player.getPlayerId() == actionDto.getActiveTurn().getActivePlayerId());
            // todo which one to choose
            seat.setSeatPlayerTheInitiator(player.getPlayerId() ==
                    actionDto.getQasinoGame().getInitiator());
            seat.setSeatPlayerTheInitiator(player.getRole() == Role.INITIATOR);
            // player stats
            seat.setSeatPlayerId(player.getPlayerId());
            seat.setSeatPlayer(player);
            seat.setSeatFiches(player.getFiches());
            // todo double or nothing? then time the turn in round
            seat.setSeatCurrentBet(player.getFiches());
            seat.setSeatPlayerAvatar(player.getAvatar());
            seat.setSeatPlayerAiLevel(player.getAiLevel());
            // player cards and moves
            List<Card> hand = player.getCards();
            List<String> handStrings =
                    hand.stream().map(Card::getRankSuit).collect(Collectors.toList());
            seat.setStringCardsInHand("[" + String.join("],[", handStrings) + "]");
            seat.setPreviousCardMoves(actionDto.getActiveTurn().getCardMoves().stream()
                    .filter(p -> p.getPlayerId() == player.getPlayerId())
                    .toList());

            // when player is human
            seat.setHuman(player.isHuman());
            if (player.isHuman()) {
                seat.setVisitorId(player.getVisitor().getVisitorId());
                seat.setVisitorName(player.getVisitor().getVisitorName());
            } else {
                seat.setVisitorId(0);
                seat.setVisitorName(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
            }

            // is player the winner

  }
        return seats;
    }

    public interface Dto {
        // @formatter:off
        // Getters
        Game getQasinoGame();
        Visitor getQasinoVisitor();
        List<Player> getQasinoGamePlayers();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();

        // Setters
        void setTable(SectionTable table);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}


