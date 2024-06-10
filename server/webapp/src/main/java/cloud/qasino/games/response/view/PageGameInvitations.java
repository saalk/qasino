package cloud.qasino.games.response.view;

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
public class PageGameInvitations {

    // buttons
    // 1 accept or decline invitation for a game
    // 2 when accepted and playable its selected game

    // Main - 1, 2
    @JsonProperty("GameInvitations")
    private List<Game> gameInvitations;

}

