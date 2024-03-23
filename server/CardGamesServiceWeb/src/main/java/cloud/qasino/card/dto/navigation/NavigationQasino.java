package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Turn;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationQasino {

    private boolean isPlayable;      // icon is fiches -> playable game to enable nav-qasino

    @JsonProperty("SelectedGame")
    private Game selectedGame; // including a list of players

    @JsonProperty("gameName")
    private String gameName; // game type and gameId concat

    private int currentRound;
    private int currentMove;

    private Turn activeTurn;


}
