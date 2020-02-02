package cloud.qasino.card.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Counters {

    @Data
    public static class Totals {

        private int users;
        private int games;
        private GameSubTotals subTotalsGames = new GameSubTotals();
        private int players;
        private int leagues;
        private int cards;
    }

    @Data
    public static class GameSubTotals {
        private int newGames;
        private int startedGames;
        private int finishedGames;
        private int errorGames;
    }
}