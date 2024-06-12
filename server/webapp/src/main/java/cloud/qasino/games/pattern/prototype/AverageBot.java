package cloud.qasino.games.pattern.prototype;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerState;
import cloud.qasino.games.database.security.Visitor;

// DumbBot extends Bot<DumbBot>, so its clone method will return a DumbBot instance
public class AverageBot extends Bot<AverageBot> {

    int seat;
    private AiLevel aiLevel;

    public AverageBot(Visitor visitor, Game game, PlayerState playerState, int fiches, int seat, Avatar avatar, String avatarName, AiLevel aiLevel) {
        super(visitor, game, playerState, fiches, seat, avatar, avatarName, aiLevel);
    }



    @Override
    public AverageBot clone() throws CloneNotSupportedException {
        // Cast to DumbBot as super returns Object
        AverageBot cloned = (AverageBot) super.clone();
//        cloned.hobbies = new ArrayList<>(this.hobbies); // Deep copying of mutable fields.
        aiLevel = AiLevel.AVERAGE;
        return cloned;
    }
}
