package cloud.qasino.games.dto.view;

import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVisitor {

    // buttons
    // 1 update visitor details
    // - input is
    //      email
    //      pawn / repay
    // 2 start a new game if no game in setup, playing
    // - input is
    //      Type highlow,
    //      ante int
    //      Avatar player
    //      (o) League
    //      (o) AiLevel for adding bot

    // Main - 1, 2
    @JsonProperty("Visitor")
    private Visitor selectedVisitor;
    // Stats - games in setup or playing are auto discovered
    // you can initiate one game in setup or playing at a time
    @JsonProperty("InitiatedGames")
    private Map<GameState, Integer> initiatedGamesPerState;
    @JsonProperty("InvitedGames")
    private Map<GameState, Integer> invitedGamesPerState;

}
