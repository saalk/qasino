package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.GameState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class GameTest extends QasinoSimulator {

    @Test
    public void givenQasinoGame_whenCreated_thenReturnValidObjectValues() {

        // default games is
//        this.state = GameState.NEW;
//        this.type = Type.HIGHLOW;
//        this.style = new Style().getLabel();
//        this.ante = 20;

        assertThat(game.getInitiator()).isEqualTo(visitor.getVisitorId());
        assertThat(game.getType()).isEqualTo(Type.HIGHLOW);
        assertThat(game.getAnte()).isEqualTo(00);
        assertThat(game.getState()).isEqualTo(GameState.INITIALIZED);

        // changes
        game.setState(GameState.PREPARED);

        // bot to seat 1
//        game.switchPlayers(0,1);
//        assertThat(game.getPlayers().get(0).getPlayerId()).isEqualTo(bot.getPlayerId());

        // player back to seat 1
//        game.switchPlayers(1,-1);
//        assertThat(game.getPlayers().get(0).getPlayerId()).isEqualTo(player.getPlayerId());

    }
}