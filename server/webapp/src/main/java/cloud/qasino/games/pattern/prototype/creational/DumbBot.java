package cloud.qasino.games.pattern.prototype.creational;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.pattern.prototype.Bot;

// DumbBot extends Bot<DumbBot>, so its clone method will return a DumbBot instance
public class DumbBot extends Bot<DumbBot> {

    int seat;
    private AiLevel aiLevel;

    public DumbBot(Visitor visitor, Game game, PlayerType playerType, int fiches, int seat, Avatar avatar, String avatarName, AiLevel aiLevel) {
        super(visitor, game, playerType, fiches, seat, avatar, avatarName, aiLevel);
    }



    @Override
    public DumbBot clone() throws CloneNotSupportedException {
        // Cast to DumbBot as super returns Object
        DumbBot cloned = (DumbBot) super.clone();
//        cloned.hobbies = new ArrayList<>(this.hobbies); // Deep copying of mutable fields.
        aiLevel = AiLevel.DUMB;
        return cloned;
    }
}
