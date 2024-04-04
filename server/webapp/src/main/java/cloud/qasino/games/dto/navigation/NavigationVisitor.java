package cloud.qasino.games.dto.navigation;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Visitor;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationVisitor {

    private boolean hasLoggedOn;     // icon is spaceship -> logon to enable nav-visitor
    // modification only possible when <hasLoggedOn = true>

    @JsonProperty("Visitor")
    private Visitor visitor;

    // list of playable initiated games
    // + boolean isSelected
    // + boolean isSelectable
    // + num/total of pending invites
    @JsonProperty("NewGames")
    private List<Game> newGames;

    // list of playable accepted games
    // + boolean isSelected
    // + boolean isSelectable
    // + num/total of pending invites
    @JsonProperty("AcceptedGames")
    private List<Game> acceptedGames;

    // list of pending invitations
    // + num/total of pending invites
    @JsonProperty("PendingInvitations")
    private List<Game> pendingInvitation;
    // list of pending invitations
    // + num/total of pending invites
    @JsonProperty("StartedGames")
    private List<Game> startedGames;
}

