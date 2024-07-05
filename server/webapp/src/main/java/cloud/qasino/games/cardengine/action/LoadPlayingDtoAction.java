package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.cardengine.cardplay.Hand;
import cloud.qasino.games.cardengine.cardplay.SeatDto;
import cloud.qasino.games.cardengine.cardplay.Table;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LoadPlayingDtoAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Autowired private PlayerService playerService;
    @Autowired private GameService gameService;
    @Autowired private PlayingService playingService;
    @Autowired private PlayingRepository playingRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    // @formatter:on

    PlayerMapper playerMapper;

    @Override
    public EventOutput.Result perform() {

        Table table = new Table();

        // FROM GAME - LEAGUE
        table.setLeagueName(getGame().getLeague().getName());
        table.setActive(getLeague().isActive());

        // FROM GAME
        table.setType(getGame().getType());
        table.setAnte(getGame().getAnte());
        table.setGameState(getGame().getState());
        table.setGameStateGroup(getGame().getGameStateGroup());
        table.setCardsInTheGameSorted(cardRepository.findByGameIdOrderBySequenceAsc(getParams().getSuppliedGameId()));
        table.setCardsInStockNotInHandSorted(
                table.getCardsInTheGameSorted().stream()
                        .filter(c -> c.getHand() == null)
                        .toList());
        table.setStringCardsInStockNotInHand(String.valueOf(table.getCardsInStockNotInHandSorted()));
        Playing playing = refreshOrFindPlayingForGame();
        if (playing == null) {
            // when game is quit before started
            table.setCountStockAndTotal("[-/-] stock/total");
            return EventOutput.Result.SUCCESS;
        } else {
            table.setCountStockAndTotal("[" + table.getCardsInStockNotInHandSorted().size() + "/" +
                    table.getCardsInTheGameSorted().size() + "] stock/total");
        }

        // FROM PLAYING
        table.setAllCardMovesForTheGame(StreamUtil.sortCardMovesOnSequenceWithStream(playing.getCardMoves(), 0));
        table.setCurrentRoundNumber(playing.getCurrentRoundNumber());
        table.setCurrentSeatNumber(playing.getCurrentSeatNumber());
        table.setCurrentMoveNumber(playing.getCurrentMoveNumber());

        // FROM PLAYING - ACTIVE AND NEXT PLAYER
        table.setPlayer(playerMapper.toDto(playing.getPlayer()));
        table.setNextPlayer(gameService.findNextPlayerForGame(getGame()));
        table.setPlayerType(table.getPlayer().getPlayerType());
        table.setFiches(table.getPlayer().getFiches());
        table.setAvatar(table.getPlayer().getAvatar());
        table.setAvatarName(table.getPlayer().getAvatarName());
        table.setAiLevel(table.getPlayer().getAiLevel());
        table.setHuman(table.getPlayer().isHuman());

        table.setSeatDtos(mapSeats(playing));
        return EventOutput.Result.SUCCESS;
    }

    private List<SeatDto> mapSeats(Playing playing) {
//
        List<SeatDto> seatDtos = new ArrayList<>();

        for (PlayerDto player : getGame().getPlayerDtos()) {
            SeatDto seatDto = new SeatDto();

            // seatDto stats
            seatDto.setSeat(player.getSeat());
            seatDto.setSeatPlaying(player.getPlayerId() == playing.getPlayer().getPlayerId());
            // todo which one to choose
//            seatDto.setSeatPlayerTheInitiator(player.getPlayerId() ==
//                    actionDto.getQasinoGame().getInitiator());
            seatDto.setSeatPlayerTheInitiator(player.getPlayerType() == PlayerType.INITIATOR);
            seatDto.setSeatFiches(player.getFiches());
            seatDto.setSeatCurrentBet(getGame().getAnte());

            // player stats
            seatDto.setPlayer(player);
            seatDto.setSeatPlayerId(player.getPlayerId());
            seatDto.setPlayerType(player.getPlayerType());
            seatDto.setAvatar(seatDto.getPlayer().getAvatar());
            seatDto.setAvatarName(seatDto.getPlayer().getAvatarName());
            seatDto.setAiLevel(seatDto.getPlayer().getAiLevel());
            seatDto.setHuman(seatDto.getPlayer().isHuman());

            // when player for the seatDto is human
            if (player.isHuman()) {
                seatDto.setVisitorId(player.getVisitor().getVisitorId());
                seatDto.setUsername(player.getVisitor().getUsername());
                seatDto.setSeatStartBalance(player.getVisitor().getBalance());
            } else {
                seatDto.setVisitorId(0);
                seatDto.setUsername(player.getAiLevel().getLabel() + " " + player.getAvatar().getLabel());
                seatDto.setSeatStartBalance(0);
            }

            seatDto.setCardsInHand(player.getCardsInHand());

            Hand handInRound = new Hand();
            List<CardMove> cardMoves = playing.getCardMoves().stream()
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
//          seatDto.setCardsInHandPerPlaying(handInRounds);
            seatDtos.add(seatDto);
        }
        return seatDtos;

    }
}


