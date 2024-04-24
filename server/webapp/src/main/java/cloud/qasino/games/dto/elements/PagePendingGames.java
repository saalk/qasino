package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Game;
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
public class PagePendingGames {

    // Main
    @JsonProperty("GamesWithPendingInvitations")
    private List<Game> gamesPendingInvitations;
    @JsonProperty("GamesWaitingToPlay")
    private List<Game> gamesPlayableYouInitiated;
    // Stats
    @JsonProperty("InitiatedGames")
    private List<Game> gamesInitiated;
    // Pending actions
    @JsonProperty("GamesWaitingForYou")
    private List<Game> gamesWaitingForYouToAccept;
    @JsonProperty("GamesYouAccepted")
    private List<Game> gamesYouAccepted;
}

