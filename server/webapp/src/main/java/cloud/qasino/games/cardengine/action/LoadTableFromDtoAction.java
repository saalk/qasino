package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.cardengine.cardplay.Seat;
import cloud.qasino.games.cardengine.cardplay.Table;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class LoadTableFromDtoAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired @Lazy private PlayerService playerService;
    @Autowired @Lazy private TurnAndCardMoveService turnAndCardMoveService;
    @Autowired private TurnRepository turnRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    // @formatter:on

    @Override
    public EventOutput.Result perform() {

        Table table = new Table();

        // FROM GAME - LEAGUE
        table.setLeagueName(getGame().getLeagueName());
        table.setActive(getLeague().isActive());

        // FROM GAME
        table.setType(getGame().getType());
        table.setAnte(getGame().getAnte());
        table.setGameState(getGame().getState());
        table.setGameStateGroup(getGame().getGameStateGroup());
        table.setCardsInTheGameSorted(cardRepository.findByGameIdOrderBySequenceAsc(getIds().getSuppliedGameId()));
        table.setCardsInStockNotInHandSorted(
                table.getCardsInTheGameSorted().stream()
                        .filter(c -> c.getHand() == null)
                        .toList());
        table.setStringCardsInStockNotInHand(String.valueOf(table.getCardsInStockNotInHandSorted()));

        // FROM TURN
        Turn turn = refreshOrFindTurnForGame();
        if (turn == null) {
            // when game is quit before started
            table.setCountStockAndTotal("[-/-] stock/total");
            return EventOutput.Result.SUCCESS;
        } else {
            table.setCountStockAndTotal("[" + table.getCardsInStockNotInHandSorted().size() + "/" +
                    table.getCardsInTheGameSorted().size() + "] stock/total");
        }
        table.setAllCardMovesForTheGame(StreamUtil.sortCardMovesOnSequenceWithStream(turn.getCardMoves(), 0));
        table.setCurrentRoundNumber(turn.getCurrentRoundNumber());
        table.setCurrentSeatNumber(turn.getCurrentSeatNumber());
        table.setCurrentMoveNumber(turn.getCurrentMoveNumber());

        // FROM TURN - ACTIVE AND NEXT PLAYER
//         table.setActivePlayer(turn.getActivePlayerId());

        table.setSeats(mapSeats());
        return EventOutput.Result.SUCCESS;
    }

    private List<Seat> mapSeats() {
//
//        List<Seat> seats = new ArrayList<>();
//        for (Player player : actionDto.getQasinoGamePlayers()) {
//            SectionSeat seat = new SectionSeat();
//            // seat stats
//            seat.setSeatId(player.getSeat());
//            seat.setPlaying(player.getPlayerId() == actionDto.getActiveTurn().getActivePlayerId());
//            // todo which one to choose
////            seat.setSeatPlayerTheInitiator(player.getPlayerId() ==
////                    actionDto.getQasinoGame().getInitiator());
//            seat.setSeatPlayerTheInitiator(player.getPlayerType() == PlayerType.INITIATOR);
//            // player stats
//            seat.setSeatPlayerId(player.getPlayerId());
//            seat.setSeatPlayer(player);
//            seat.setSeatVisitor(player.getVisitor());
//            seat.setSeatFiches(player.getFiches());
//            // TODO make double or nothing work
//            seat.setSeatCurrentBet(actionDto.getQasinoGame().getAnte());
//            seat.setSeatPlayerAvatar(player.getAvatar());
//            seat.setSeatPlayerAiLevel(player.getAiLevel());
//            // player cards and moves
//            List<Card> hand = player.getCards();
//            List<String> handStrings =
//                    hand.stream().map(Card::getRankSuit).collect(Collectors.toList());
//            seat.setStringCardsInHand("[" + String.join("],[", handStrings) + "]");
//
//            List<CardMove> cardMoves = actionDto.getActiveTurn().getCardMoves().stream()
//                    .filter(p -> p.getPlayerId() == player.getPlayerId())
//                    .toList();
//
//            boolean first = true;
//            int round = 0;
//            List<SectionHand> handInRounds = new ArrayList<>();
//            SectionHand handInRound = new SectionHand();
//            List<String> cards = new ArrayList<>();
//            // card Moves should be ordered
//            for (CardMove move : cardMoves) {
//                round = move.getRoundFromSequence();
//                if (round == move.getRoundFromSequence()) {
//                    handInRound.setRoundNumber(move.getRoundFromSequence());
//                    cards.add(move.getCardMoveDetails());
//                } else {
//                    // first or new round
//                    if (first) {
//                        // first
//                        handInRound.setRoundNumber(move.getRoundFromSequence());
//                        cards.add(move.getCardMoveDetails());
//                        first = false;
//                    } else {
//                        // new round - first update and save last
//                        handInRound.setCardsInRound(String.valueOf(cards));
//                        handInRounds.add(handInRound);
//                        //
//                        handInRound = new SectionHand();
//                        round = move.getRoundFromSequence();
//
//                        handInRound.setRoundNumber(move.getRoundFromSequence());
//                        cards.clear();
//                        cards.add(move.getCardMoveDetails());
//                    }
//                }
//            }
//            seat.setCardsInHandPerTurn(handInRounds);
//
////          private Map<String, String> handInRound;
//            // when player is human
//            seat.setHuman(player.isHuman());
//            if (player.isHuman()) {
//                seat.setVisitorId(player.getVisitor().getVisitorId());
//                seat.setUsername(player.getVisitor().getUsername());
//                seat.setSeatStartBalance(player.getVisitor().getBalance());
//            } else {
//                seat.setVisitorId(0);
//                seat.setSeatStartBalance(0);
//                seat.setUsername(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
//            }
//            seats.add(seat);
//            log.warn("tabel seat " + seat.getSeatId()+ "set ");
        // is player the winner
        return null;
    }
//    return seats;

}


