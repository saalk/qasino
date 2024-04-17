package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import org.junit.jupiter.api.Test;

import static java.util.EnumSet.of;
import static org.assertj.core.api.Assertions.assertThat;


class CardMoveTest extends QasinoSimulator {

    @Test
    public void givenQasinoCardMove_whenCreated_thenReturnValidObjectValues() {

        /*
        GAME TRIGGERS result in a new GAMESTATE
        ============
        public static Set<GameTrigger> gamesTriggerNew = of(NEW, INVITE, ACCEPT, PREPARE);
        public static Set<GameTrigger> gamesTriggerPlaying = of(PLAY);
        public static Set<GameTrigger> gamesTriggerEnding = of(WINNER, LEAVE);
        public static Set<GameTrigger> gamesTriggerError = of(ABANDON, CRASH);
        */

        /*
        EVENT TRIGGERS result in a new EVENTSTATE
        ==============
        public static Set<EventTrigger> eventsBlackJack = of(DEAL, SPLIT, STOP);
        public static Set<EventTrigger> eventsHighLow = of(START, HIGHER, LOWER, STOP);
        public static Set<EventTrigger> eventsSystem = of(CRASH, WINNER, NO_CARDS_LEFT);
        */

        // 1. get current player in the turn - assume player for now
        long currentPlayer = turn.getActivePlayerId();
        assertThat(currentPlayer).isEqualTo(player.getPlayerId());

        // 2a. deal (the top) card for the game face up
        Card firstCardDealt = game.dealCard(player, Face.UP);

        assertThat(firstCardDealt.getCardId()).isEqualTo(game.getCards().get(0).getCardId());
        assertThat(firstCardDealt.getFace()).isEqualTo(Face.UP);
        assertThat(firstCardDealt.getSequence()).isEqualTo(1);
        assertThat(firstCardDealt.getHand()).isEqualTo(player);

        // 2b. move the first card to the hand of the current player
        CardMove cardMove = new CardMove(turn, player, firstCardDealt.getCardId(), Move.DEAL, Location.HAND);
        cardMove.setRoundNumber(turn.getCurrentRoundNumber());
        cardMove.setMoveNumber(turn.getCurrentMoveNumber());

        assertThat(cardMove.getMove()).isEqualTo(Move.DEAL);
        assertThat(cardMove.getRoundNumber()).isEqualTo(1);
        assertThat(cardMove.getMoveNumber()).isEqualTo(1);

        // 3. get the current players  expectation
        int bet = 10;
        // todo should not be a move but an expectation
        Move expectation = Move.HIGHER;

        // 4a. deal the second card face up
        Card secondCardDealt = game.dealCard(player, Face.UP);
        // TODO make this a method
        turn.setCurrentMoveNumber(turn.getCurrentMoveNumber()+1);

        assertThat(secondCardDealt.getCardId()).isEqualTo(game.getCards().get(1).getCardId());
        assertThat(secondCardDealt.getFace()).isEqualTo(Face.UP);
        assertThat(secondCardDealt.getSequence()).isEqualTo(2);
        assertThat(secondCardDealt.getHand()).isEqualTo(player);

        // 4b. move the second card to the hand of the current player
        cardMove = new CardMove(turn, player, secondCardDealt.getCardId(), expectation, Location.HAND);
        cardMove.setRoundNumber(turn.getCurrentRoundNumber());
        cardMove.setMoveNumber(turn.getCurrentMoveNumber());

        assertThat(cardMove.getMove()).isEqualTo(Move.HIGHER);
        assertThat(cardMove.getRoundNumber()).isEqualTo(1);
        assertThat(cardMove.getMoveNumber()).isEqualTo(2);

        // 5. calculate outcome for expectation
        // TODO

    }

}