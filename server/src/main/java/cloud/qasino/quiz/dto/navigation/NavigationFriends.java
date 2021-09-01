package cloud.qasino.quiz.dto.navigation;

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
public class NavigationFriends {

    @JsonProperty("SearchFriend")
    private User user;

    private int totalFriends;
    private int pendingInvites;

    @JsonProperty("Friends")
    private List<User> acceptedFriends;

    @JsonProperty("PendingInvitation")
    private List<User> pendingInvitations;

    //private List<Chat> chats;
    // list of chats per Friend
}
