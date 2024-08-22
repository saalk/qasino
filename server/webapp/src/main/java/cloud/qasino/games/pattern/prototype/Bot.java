package cloud.qasino.games.pattern.prototype;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.security.Visitor;

public abstract class Bot<T> extends Player implements Cloneable {

    private boolean human;

    public Bot(Visitor visitor, Game game, PlayerType playerType, int fiches, int seat, Avatar avatar, String avatarName, AiLevel aiLevel) {
        super(visitor, game, playerType, fiches, fiches, seat, avatar, avatarName, aiLevel);
        human = false;
    }

    @Override
    // Implements clone in abstract class to make use of generics
    protected T clone() throws CloneNotSupportedException {
        return (T) super.clone();
    }
}
