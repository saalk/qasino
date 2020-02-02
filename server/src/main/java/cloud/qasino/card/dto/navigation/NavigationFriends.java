package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NavigationFriends {

    @JsonProperty("SearchFriend")
    private User user;

    @JsonProperty("Friends")
    private List<User> acceptedFriends;

    @JsonProperty("PendingInvitation")
    private List<User> pendingInvitation;

    //private List<Chat> chats;
    // list of chats per Friend
}
