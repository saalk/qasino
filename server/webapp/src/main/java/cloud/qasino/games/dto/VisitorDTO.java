package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.security.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Data
/**
 * The purpose of using this DTO is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class VisitorDTO {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    private long visitorId;
    @NotBlank(message = VisitorDTO.NOT_BLANK_MESSAGE)
    private String username;
    private String password;
  private Collection<Role> roles;
//  Role adminRole = roleRepository.findByName("ROLE_ADMIN");
    boolean isAdmin;
    boolean isUser;
    @NotBlank(message = VisitorDTO.NOT_BLANK_MESSAGE)
    private String alias;
    private int aliasSequence;
    @NotBlank(message = VisitorDTO.NOT_BLANK_MESSAGE)
    @Email(message = VisitorDTO.EMAIL_MESSAGE)
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


