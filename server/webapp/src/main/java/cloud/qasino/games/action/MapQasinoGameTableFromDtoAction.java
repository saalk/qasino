package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import cloud.qasino.games.response.view.SectionHand;
import cloud.qasino.games.response.view.SectionSeat;
import cloud.qasino.games.response.view.SectionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MapQasinoGameTableFromDtoAction implements Action<MapQasinoGameTableFromDtoAction.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        SectionTable table = new SectionTable();

        if ((actionDto.getActivePlaying() == null)) {
            if ((actionDto.getQasinoGame() != null)) {
                actionDto.setActivePlaying(actionDto.getQasinoGame().getPlaying());
                if ((actionDto.getActivePlaying() == null)) {
                    // when game is quit before started
                    table.setCountStockAndTotal(
                            "[-/-] stock/total");
                    table.setPlaying(null);
                    table.setSeats(null);
                    return EventOutput.Result.SUCCESS;
                }
            } else {
                // when game is quit before started
                table.setCountStockAndTotal(
                        "[-/-] stock/total");
                table.setPlaying(null);
                table.setSeats(null);
                return EventOutput.Result.SUCCESS;
            }
        }

        table.setPlaying(actionDto.getActivePlaying());
        List<Card> stockNotInHand =
                actionDto.getCardsInTheGameSorted()
                        .stream()
                        .filter(c -> c.getHand() == null)
                        .toList();
//        table.setCardsInStockNotInHand(stockNotInHand);
        table.setStringCardsInStockNotInHand(String.valueOf(stockNotInHand));
        table.setCountStockAndTotal(
                "[" + stockNotInHand.size() +
                        "/" + actionDto.getCardsInTheGameSorted().size() +
                        "] stock/total");
        table.setSeats(null);
        table.setSeats(mapSeats(actionDto));
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
            seat.setPlaying(player.getPlayerId() == actionDto.getActivePlaying().getPlayer().getPlayerId());
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
            seat.setSeatPlayerAvatarName(player.getAvatarName());
            seat.setSeatPlayerAiLevel(player.getAiLevel());
            // player cards and moves
            List<Card> hand = player.getCards();
            List<String> handStrings =
                    hand.stream().map(Card::getRankSuit).collect(Collectors.toList());
            seat.setCardsInHand("[" + String.join("],[", handStrings) + "]");

            List<CardMove> cardMoves = actionDto.getActivePlaying().getCardMoves().stream()
                    .filter(p -> p.getPlayerId() == player.getPlayerId())
                    .toList();

            boolean first = true;
            int round = 0;
            List <SectionHand> handInRounds = new ArrayList<>();
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
            seat.setCardsInHandPerPlaying(handInRounds);

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
        return seats;
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();

        // Getters
        Game getQasinoGame();
        Visitor getQasinoVisitor();
        List<Player> getQasinoGamePlayers();
        Player getPlayingPlayer();
        Playing getActivePlaying();
        List<Card> getCardsInTheGameSorted();

        // Setters
        void setActivePlaying(Playing playing);
        void setPlayingPlayer(Player playingPlayer);
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


