package cloud.qasino.card.entity;

import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator= JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users", indexes =
        { @Index(name = "users_index", columnList = "user_id", unique = true),
          @Index(name = "alias_index", columnList = "alias", unique = false )
        })
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private int userId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    // Foreign keys

    // Normal fields

    @Column(name = "alias", length = 50, nullable = false)
    private String alias;

    @Column(name = "alias_seq")
    private int aliasSequence;


    @Column(name = "email", length = 50, nullable = true)
    private String email;

    @Column(name = "balance")
    private int balance;

    @Column(name = "secured_loan")
    private int securedLoan;

    @Setter(AccessLevel.NONE)
    @Column(name = "year", length = 4)
    private int year;

    @Setter(AccessLevel.NONE)
    @Column(name = "month", length = 20)
    private Month month;

    @Setter(AccessLevel.NONE)
    @Column(name = "week", length = 3)
    private String week;

    @Setter(AccessLevel.NONE)
    @Column(name = "day", length = 2)
    private int day;

    // References

    // PL: a User becomes a Player when playing a Game
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    // just a reference, the actual fk column is in player not here!
    // However ai players are no users!
    private List<Player> players;

    @JsonIgnore
    // UsPl: a User can start many Leagues
    // However bots cannot start a league
    @OneToMany(mappedBy = "user", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in league not here!
    private List<League> leagues;

    public User() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        this.week = localDateAndTime.format(week);
        this.day = localDateAndTime.getDayOfMonth();

        this.alias = "alias";
    }

    public User(String alias, int aliasSequence, String email) {
        this();
        this.alias = alias;
        this.aliasSequence = aliasSequence;
        this.email = email;
    }

    public boolean repayLoan(){

        if (this.balance >= this.securedLoan) {
            this.balance = --this.securedLoan;
            this.securedLoan = 0;
            return true;
        }
        return false;
    }

    public boolean pawnShip(int max) {

        if (this.securedLoan == 0) {
            int seed = max == 0 ? 1001 : max + 1;
            Random random = new Random();
            this.securedLoan = random.nextInt(seed);
            this.balance = ++ this.securedLoan;
            return true;
        }
        return false;
    }

    public String winCount(List<Player> playersPlayed) {

        int won = 0;

        for (Player player : playersPlayed) {
            if (player.isWinner()) {
                won++;
            }
        }

        return "won/total: [" + won + "/"+ playersPlayed.size()+"]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}


