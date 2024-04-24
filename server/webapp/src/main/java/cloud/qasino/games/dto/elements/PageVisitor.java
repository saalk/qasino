package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.game.GameState;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageVisitor {

    // Main
    @JsonProperty("Visitor")
    private Visitor selectedVisitor;
    // Stats
    private int gamesPlayed;
    private int gamesWon;
    @JsonProperty("initiatedGamesPerGame")
    private Map<GameState, Integer> gamesPerState;
    // Pending actions
    @JsonProperty("PendingInvitation")
    private List<Player> pendingInvitations;
}
