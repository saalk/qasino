package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.cardengine.cardplay.Hand;
import cloud.qasino.games.cardengine.cardplay.Seat;
import cloud.qasino.games.cardengine.cardplay.Table;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.PlayerMapperImpl;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LoadTableFromAllOtherDtosAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired @Lazy private PlayerService playerService;
    @Autowired @Lazy private GameService gameService;
    @Autowired @Lazy private TurnAndCardMoveService turnAndCardMoveService;
    @Autowired private TurnRepository turnRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    // @formatter:on

    PlayerMapper playerMapper;

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
        Turn turn = refreshOrFindTurnForGame();
        if (turn == null) {
            // when game is quit before started
            table.setCountStockAndTotal("[-/-] stock/total");
            return EventOutput.Result.SUCCESS;
        } else {
            table.setCountStockAndTotal("[" + table.getCardsInStockNotInHandSorted().size() + "/" +
                    table.getCardsInTheGameSorted().size() + "] stock/total");
        }

        // FROM TURN
        table.setAllCardMovesForTheGame(StreamUtil.sortCardMovesOnSequenceWithStream(turn.getCardMoves(), 0));
        table.setCurrentRoundNumber(turn.getCurrentRoundNumber());
        table.setCurrentSeatNumber(turn.getCurrentSeatNumber());
        table.setCurrentMoveNumber(turn.getCurrentMoveNumber());

        // FROM TURN - ACTIVE AND NEXT PLAYER
        table.setActivePlayer(playerMapper.toDto(turn.getActivePlayer()));
        table.setNextPlayer(gameService.findNextPlayerForGame(getGame()));
        table.setPlayerType(table.getActivePlayer().getPlayerType());
        table.setFiches(table.getActivePlayer().getFiches());
        table.setAvatar(table.getActivePlayer().getAvatar());
        table.setAvatarName(table.getActivePlayer().getAvatarName());
        table.setAiLevel(table.getActivePlayer().getAiLevel());
        table.setHuman(table.getActivePlayer().isHuman());

        table.setSeats(mapSeats(turn));
        return EventOutput.Result.SUCCESS;
    }

    private List<Seat> mapSeats(Turn turn) {
//
        List<Seat> seats = new ArrayList<>();

        for (PlayerDto player : getGame().getPlayerDtos()) {
            Seat seat = new Seat();

            // seat stats
            seat.setSeatId(player.getSeat());
            seat.setSeatPlaying(player.getPlayerId() == turn.getActivePlayer().getPlayerId());
            // todo which one to choose
//            seat.setSeatPlayerTheInitiator(player.getPlayerId() ==
//                    actionDto.getQasinoGame().getInitiator());
            seat.setSeatPlayerTheInitiator(player.getPlayerType() == PlayerType.INITIATOR);
            seat.setSeatFiches(player.getFiches());
            seat.setSeatCurrentBet(getGame().getAnte());

            // player stats
            seat.setPlayer(player);
            seat.setSeatPlayerId(player.getPlayerId());
            seat.setPlayerType(player.getPlayerType());
            seat.setAvatar(seat.getPlayer().getAvatar());
            seat.setAvatarName(seat.getPlayer().getAvatarName());
            seat.setAiLevel(seat.getPlayer().getAiLevel());
            seat.setHuman(seat.getPlayer().isHuman());

            // when player for the seat is human
            if (player.isHuman()) {
                seat.setVisitorId(player.getVisitorId());
                seat.setUsername(player.getVisitor().getUsername());
                seat.setSeatStartBalance(player.getVisitor().getBalance());
            } else {
                seat.setVisitorId(0);
                seat.setUsername(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
                seat.setSeatStartBalance(0);
            }

            // player cards and moves
            List<Card> hand = player.getCards();
            List<String> handStrings =
                    hand.stream().map(Card::getRankSuit).collect(Collectors.toList());
            seat.setAllCardsInHand("[" + String.join("],[", handStrings) + "]");

            Hand handInRound = new Hand();
            List<CardMove> cardMoves = turn.getCardMoves().stream()
                    .filter(p -> p.getPlayerId() == player.getPlayerId())
                    .toList();

            boolean first = true;
            int round = 0;
            List<Hand> handInRounds = new ArrayList<>();
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
                        handInRound.setCardsInRoundAndSeat(String.valueOf(cards));
                        handInRounds.add(handInRound);
                        //
                        handInRound = new Hand();
                        round = move.getRoundFromSequence();

                        handInRound.setRoundNumber(move.getRoundFromSequence());
                        cards.clear();
                        cards.add(move.getCardMoveDetails());
                    }
                }
            }
//          seat.setCardsInHandPerTurn(handInRounds);
            seats.add(seat);
        }
        return seats;

    }
}


