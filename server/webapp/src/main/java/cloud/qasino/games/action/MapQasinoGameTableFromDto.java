package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.dto.elements.SectionSeat;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapQasinoGameTableFromDto implements Action<MapQasinoGameTableFromDto.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        SectionTable table = new SectionTable();
        List<SectionSeat> seats = new ArrayList<>();

        if ((actionDto.getActiveTurn() == null)) {
            if ((actionDto.getQasinoGame() != null)) {
                actionDto.setActiveTurn(actionDto.getQasinoGame().getTurn());
            } else {
                return EventOutput.Result.SUCCESS;
            }
        }
        if ((actionDto.getActiveTurn() == null)) {
            return EventOutput.Result.SUCCESS;
        }
        table.setCurrentTurn(actionDto.getActiveTurn());
        table.setPossibleMoves(new ArrayList<>());
        switch (actionDto.getQasinoGame().getType()) {
            case HIGHLOW -> {
                if (actionDto.getTurnPlayer().getAiLevel().equals(AiLevel.HUMAN)) {
                    table.getPossibleMoves().add(Move.HIGHER);
                    table.getPossibleMoves().add(Move.LOWER);
                    table.getPossibleMoves().add(Move.PASS);
                } else {
                    table.getPossibleMoves().add(Move.NEXT);
                }
            }
            case BLACKJACK -> {
                if (actionDto.getTurnPlayer().getAiLevel().equals(AiLevel.HUMAN)) {
                    table.getPossibleMoves().add(Move.DEAL);
                    table.getPossibleMoves().add(Move.DOUBLE);
                    table.getPossibleMoves().add(Move.STAND);
                } else {
                    table.getPossibleMoves().add(Move.NEXT);
                }
            }
        }
        List<Card> stockNotInHand =
                actionDto.getCardsInTheGameSorted()
                        .stream()
                        .filter(c -> c.getHand() == null)
                        .collect(Collectors.toList());
//        table.setCardsInStockNotInHand(stockNotInHand);
        table.setStringCardsInStockNotInHand(String.valueOf(stockNotInHand));
        table.setCountStockAndTotal(
                "[" + stockNotInHand.size() +
                "/" + actionDto.getCardsInTheGameSorted().size() +
                "] stock/total");
        table.setSeats(mapSeats(actionDto, seats));
        actionDto.setTable(table);
        log.info("table set");
        return EventOutput.Result.SUCCESS;
    }

    private List<SectionSeat> mapSeats(Dto actionDto, List<SectionSeat> seats) {

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
                seat.setUsername(player.getVisitor().getUsername());
            } else {
                seat.setVisitorId(0);
                seat.setUsername(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
            }
            seats.add(seat);
            log.info("tabel seat " + seat.getSeatId()+ "set ");
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
        Player getTurnPlayer();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();

        // Setters
        void setActiveTurn(Turn turn);
        void setTable(SectionTable table);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}


