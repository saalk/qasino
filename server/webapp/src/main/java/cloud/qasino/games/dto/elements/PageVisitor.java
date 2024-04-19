package cloud.qasino.games.dto.elements;

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
public class PageVisitor {

    private boolean visitorIsLoggedOn;

    @JsonProperty("You")
    private Visitor visitor;

    private int totalAcceptedInvitations;
    private int totalPendingInvitations;

    public int totalNewGames;
    public int totalStartedGames;
    public int totalsFinishedGames;


    @JsonProperty("Friends")
    private List<Visitor> acceptedInvitations;

    @JsonProperty("PendingInvitation")
    private List<Visitor> pendingInvitations;

    //private List<Chat> chats;
    // list of chats per Friend
}
