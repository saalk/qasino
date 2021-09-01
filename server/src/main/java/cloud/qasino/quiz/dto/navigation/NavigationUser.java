package cloud.qasino.quiz.dto.navigation;

import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.User;
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
public class NavigationUser {

    private boolean hasLoggedOn;     // icon is spaceship -> logon to enable nav-user
    // modification only possible when <hasLoggedOn = true>

    @JsonProperty("User")
    private User user;

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

