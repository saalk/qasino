package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Visitor;
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
    // Stats - games in setup, playing are auto discovered
    @JsonProperty("InitiatedGames")
    private Map<GameState, Integer> initiatedGamesPerState;
    @JsonProperty("InvitedGames")
    private Map<GameState, Integer> invitedGamesPerState;

}
