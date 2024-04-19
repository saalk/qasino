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

    private boolean visitorHasPendingGames;

    @JsonProperty("YourInitiatedGames")
    private List<Game> gamesInitiated;

    @JsonProperty("YourGamesWithPendingInvitations")
    private List<Game> gamesPendingInvitations;

    @JsonProperty("YourGamesWaitingToPlay")
    private List<Game> gamesPlayableYouInitiated;

    @JsonProperty("GamesWaitingForYou")
    private List<Game> gamesWaitingForYouToAccept;

    @JsonProperty("GamesYouAccepted")
    private List<Game> gamesYouAccepted;
}

