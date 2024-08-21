package cloud.qasino.games.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Month;
import java.util.List;

@Getter
@Setter
/**
 * The purpose of using this Dto is to separate the internal representation of visitor data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class VisitorShortDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // core
    private long visitorId;
//    private String created; // ignore

    // ref
//    private List<RoleDto> rolesList;
//    private String roles;

    // derived
//    private List<GameDto> initiatedGamesForVisitor;
//    private List<GameDto> invitedGamesForVisitor;
    boolean isAdmin;
    boolean isUser;
    private boolean isRepayPossible;

    // Normal fields
    private String username;
//    private String password; //ignore
//    private boolean enabled; // ignore
//    private boolean isUsing2FA; // ignore
    private String alias;
    private int aliasSequence;
    private String email;
    private int balance;
    private int securedLoan;


    private int year;
    private Month month;
    private String week;
    private int weekday;

//    private List<PlayerDto> players; // ignore
//    private List<LeagueDto> leagues; // ignore

}


