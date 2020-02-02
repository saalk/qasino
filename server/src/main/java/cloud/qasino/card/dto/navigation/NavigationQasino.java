package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.Game;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NavigationQasino {

    private boolean isPlayable;      // icon is fiches -> playable game to enable nav-qasino

    @JsonProperty("gameName")
    private String gameName; // game type and gameId concat

    @JsonProperty("SelectedGame")
    private Game selectedGame; // including a list of players

}
