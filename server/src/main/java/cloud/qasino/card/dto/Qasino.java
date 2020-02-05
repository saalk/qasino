package cloud.qasino.card.dto;

import cloud.qasino.card.dto.enums.Enums;
import cloud.qasino.card.dto.navigation.*;
import cloud.qasino.card.dto.statistics.Counter;
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
public class Qasino {

    @JsonProperty("NavBarItems")
    private List<NavigationBarItem> navBarItems;

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

    // extra
    @JsonProperty("Enums")
    Enums enums = new Enums();
    @JsonProperty("Counters")
    Counter counter =new Counter();


}
