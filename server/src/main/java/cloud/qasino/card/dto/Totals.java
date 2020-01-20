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
    @JsonProperty("TotalPlayers")
    private int players;
    @JsonProperty("TotalLeagues")
    private int leagues;
    @JsonProperty("TotalPlayingCards")
    private int playingCards;
    public Totals() {

    }
}