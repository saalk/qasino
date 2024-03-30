package cloud.qasino.games.dto.navigation;

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
public class NavigationFriends {

    @JsonProperty("SearchFriend")
    private Visitor visitor;

    private int totalFriends;
    private int pendingInvites;

    @JsonProperty("Friends")
    private List<Visitor> acceptedFriends;

    @JsonProperty("PendingInvitation")
    private List<Visitor> pendingInvitations;

    //private List<Chat> chats;
    // list of chats per Friend
}
