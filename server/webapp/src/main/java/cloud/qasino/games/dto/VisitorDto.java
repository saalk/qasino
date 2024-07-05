package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Role;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.time.Month;
import java.util.Collection;
import java.util.List;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of visitor data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class VisitorDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    private long visitorId;
    @NotBlank(message = VisitorDto.NOT_BLANK_MESSAGE)
    private String username;
    private String password;
  private Collection<Role> roles;
//  Role adminRole = roleRepository.findByName("ROLE_ADMIN");
    boolean isAdmin;
    boolean isUser;
    @NotBlank(message = VisitorDto.NOT_BLANK_MESSAGE)
    private String alias;
    private int aliasSequence;
    @NotBlank(message = VisitorDto.NOT_BLANK_MESSAGE)
    @Email(message = VisitorDto.EMAIL_MESSAGE)
    private String email;

    // for view
    private int balance;
    private int securedLoan;
    private boolean isRepayPossible;

    private int year;
    private Month month;
    private String week;
    private int weekday;

    private List<Game> initiatedGamesForVisitor;
    private List<Game> invitedGamesForVisitor;
    private List<League> leaguesForVisitor;

}


