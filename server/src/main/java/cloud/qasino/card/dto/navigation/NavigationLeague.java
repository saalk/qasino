package cloud.qasino.card.dto.navigation;

import cloud.qasino.card.entity.League;
import cloud.qasino.card.entity.User;
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
public class NavigationLeague {

    private boolean hasLeague;      // icon is 1/2/3 -> position in league or no league

    @JsonProperty("CreatedLeagues")
    private List<League> leagues;
    // list of active Leagues to choose results from
    // + boolean youInitiated
    // + enddate
    // + num playing / total games

    @JsonProperty("UserForLeagues")
    private List<User> users;
    // list of users and scores per league
    // + int percentageBotWins
    // + int percentageUserWins

    @JsonProperty("NewLeague")
    // creating is always possible
    private League newLeague;
}

