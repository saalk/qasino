package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.response.view.SectionHand;
import cloud.qasino.games.response.view.SectionSeat;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoadTableFromDtoAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired @Lazy PlayerService playerService;
    @Autowired @Lazy TurnAndCardMoveService turnAndCardMoveService;
    // @formatter:on

    @Override
    public EventOutput.Result perform() {

        if (getIds().getSuppliedGameId() == 0
                && getGame() == null
                && (getGame().getGameStateGroup() == GameStateGroup.SETUP ||
                getGame().getGameStateGroup() == GameStateGroup.PREPARED)
        ) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedTurnEvent());
            throw new MyNPException("69 getVisitorSupplied", "visitorId [" + super.getIds().getSuppliedVisitorId() + "]");
        }

        Turn turn = turnAndCardMoveService.
        if ((actionDto.getActiveTurn() == null)) {
            if ((actionDto.getQasinoGame() != null)) {
                actionDto.setActiveTurn(actionDto.getQasinoGame().getTurn());
                if ((actionDto.getActiveTurn() == null)) {

                }
                if (getGame().get)
                    // when game is quit before started
                    table.setCountStockAndTotal(
                            "[-/-] stock/total");
                    table.setCurrentTurn(null);
                    table.setSeats(null);
                    return EventOutput.Result.SUCCESS;

                }
            } else {
                // when game is quit before started
                table.setCountStockAndTotal(
                        "[-/-] stock/total");
                table.setCurrentTurn(null);
                table.setSeats(null);
                return EventOutput.Result.SUCCESS;
            }
        }

        table.setCurrentTurn(actionDto.getActiveTurn());
        List<Card> stockNotInHand =
                actionDto.getCardsInTheGameSorted()
                        .stream()
                        .filter(c -> c.getHand() == null)
                        .toList();
//        table.setCardsInStockNotInHand(stockNotInHand);
        table.setStringCardsInStockNotInHand(String.valueOf(stockNotInHand));
        table.setCountStockAndTotal(
                "[" + stockNotInHand.size() +
                        "/" + actionDto.getCardsInTheGameSorted().

                        size() +
                        "] stock/total");
        table.setSeats(null);
        table.setSeats(

                mapSeats(actionDto));
        actionDto.setTable(table);
//        log.warn("table set");
        return EventOutput.Result.SUCCESS;
    }

    private List<SectionSeat> mapSeats(Dto actionDto) {

        List<SectionSeat> seats = new ArrayList<>();
        for (Player player : actionDto.getQasinoGamePlayers()) {
            SectionSeat seat = new SectionSeat();
            // seat stats
            seat.setSeatId(player.getSeat());
            seat.setPlaying(player.getPlayerId() == actionDto.getActiveTurn().getActivePlayerId());
            // todo which one to choose
//            seat.setSeatPlayerTheInitiator(player.getPlayerId() ==
//                    actionDto.getQasinoGame().getInitiator());
            seat.setSeatPlayerTheInitiator(player.getPlayerType() == PlayerType.INITIATOR);
            // player stats
            seat.setSeatPlayerId(player.getPlayerId());
            seat.setSeatPlayer(player);
            seat.setSeatVisitor(player.getVisitor());
            seat.setSeatFiches(player.getFiches());
            // TODO make double or nothing work
            seat.setSeatCurrentBet(actionDto.getQasinoGame().getAnte());
            seat.setSeatPlayerAvatar(player.getAvatar());
            seat.setSeatPlayerAiLevel(player.getAiLevel());
            // player cards and moves
            List<Card> hand = player.getCards();
            List<String> handStrings =
                    hand.stream().map(Card::getRankSuit).collect(Collectors.toList());
            seat.setStringCardsInHand("[" + String.join("],[", handStrings) + "]");

            List<CardMove> cardMoves = actionDto.getActiveTurn().getCardMoves().stream()
                    .filter(p -> p.getPlayerId() == player.getPlayerId())
                    .toList();

            boolean first = true;
            int round = 0;
            List<SectionHand> handInRounds = new ArrayList<>();
            SectionHand handInRound = new SectionHand();
            List<String> cards = new ArrayList<>();
            // card Moves should be ordered
            for (CardMove move : cardMoves) {
                round = move.getRoundFromSequence();
                if (round == move.getRoundFromSequence()) {
                    handInRound.setRoundNumber(move.getRoundFromSequence());
                    cards.add(move.getCardMoveDetails());
                } else {
                    // first or new round
                    if (first) {
                        // first
                        handInRound.setRoundNumber(move.getRoundFromSequence());
                        cards.add(move.getCardMoveDetails());
                        first = false;
                    } else {
                        // new round - first update and save last
                        handInRound.setCardsInRound(String.valueOf(cards));
                        handInRounds.add(handInRound);
                        //
                        handInRound = new SectionHand();
                        round = move.getRoundFromSequence();

                        handInRound.setRoundNumber(move.getRoundFromSequence());
                        cards.clear();
                        cards.add(move.getCardMoveDetails());
                    }
                }
            }
            seat.setCardsInHandPerTurn(handInRounds);

//          private Map<String, String> handInRound;
            // when player is human
            seat.setHuman(player.isHuman());
            if (player.isHuman()) {
                seat.setVisitorId(player.getVisitor().getVisitorId());
                seat.setUsername(player.getVisitor().getUsername());
                seat.setSeatStartBalance(player.getVisitor().getBalance());
            } else {
                seat.setVisitorId(0);
                seat.setSeatStartBalance(0);
                seat.setUsername(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
            }
            seats.add(seat);
//            log.warn("tabel seat " + seat.getSeatId()+ "set ");
            // is player the winner

        }
//    return seats;
        return EventOutput.Result.SUCCESS;

    }


