package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NavigationUser {

    private boolean hasLoggedOn;     // icon is spaceship -> logon to enable nav-user
    // modification only possible when <hasLoggedOn = true>

    @JsonProperty("User")
    private User user;

    // list of playable initiated games
    // + boolean isSelected
    // + boolean isSelectable
    // + num/total of pending invites
    @JsonProperty("InitiatedGames")
    private List<Game> initiatedGames;

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
}

