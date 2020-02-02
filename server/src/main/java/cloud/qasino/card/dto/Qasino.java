package cloud.qasino.card.dto;

import cloud.qasino.card.dto.navigation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Qasino {

    @JsonProperty("NavBarItems")
    private List<NavigationBarItems> navBarItems;

    @JsonProperty("UserData")
    private NavigationUser userData;
    @JsonProperty("GameData")
    private NavigationGame gameData;
    @JsonProperty("QasinoData")
    private NavigationQasino qasinoData;
    @JsonProperty("LeagueData")
    private NavigationLeague leagueData;
    @JsonProperty("FriendsData")
    private NavigationFriends friendsData;

    @JsonProperty("Table")
    private Table table;

}
