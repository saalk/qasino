package cloud.qasino.games.controller.thymeleaf;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.repository.LeagueRepository;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@Getter
@Setter
public class SetupGameForm {

    @Autowired
    public SetupGameForm(
            LeagueRepository leagueRepository
    ) {
        this.leagueRepository = leagueRepository;
    }
    private LeagueRepository leagueRepository;

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String INVALID_MESSAGE = "{invalid.message}";

    private String visitorIdFk;

    private String type;

    private String style;

    @NotBlank(message = SetupGameForm.NOT_BLANK_MESSAGE)
    private String ante;

    private String avatar;

    private String aiLevel;
//    private String leagueId;

    public Game createGame() {
        return new Game.Builder()
//                .withLeague(leagueRepository.findLeagueByLeagueId(Long.parseLong(getLeagueId())).get())
                .withVisitorId(Long.parseLong(getVisitorIdFk()))
                .withType(getType())
                .withStyle(getStyle())
                .withAnte(Integer.parseInt(getAnte()))
                .build();
    }

}
