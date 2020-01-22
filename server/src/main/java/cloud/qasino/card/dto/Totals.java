package cloud.qasino.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Totals {

    @JsonProperty("TotalUsers")
    private int users;

    @JsonProperty("TotalGames")
    private int games;
    private int newGames;
    private int activeGames;
    private int finishedGames;
    private int errorGames;

    @JsonProperty("TotalPlayers")
    private int players;
    private int bots;
    private int invitedpLayers;

    @JsonProperty("TotalLeagues")
    private int leagues;
    private int activeLeagues;
    private int finishedLeagues;

    @JsonProperty("TotalPlayingCards")
    private int playingCards;
    private int usedPlayingCards;
    private int unusedPlayingCards;
    public Totals() {

    }
}