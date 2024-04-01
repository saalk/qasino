package cloud.qasino.games.database.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "visitor", indexes =
        {@Index(name = "visitors_index", columnList = "visitor_id", unique = true),
                @Index(name = "visitorName_index", columnList = "visitorName", unique = false)
        })
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visitor_id", nullable = false)
    private long visitorId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    // Foreign keys

    // Normal fields

    @Column(name = "visitorName", length = 50, nullable = false)
    private String visitorName;

    @Column(name = "visitorName_seq")
    private int visitorNameSequence;


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

    // PL: a Visitor becomes a Player when playing a GameSubTotals
    @JsonIgnore
    @OneToMany(mappedBy = "visitor", cascade = CascadeType.REMOVE)
    // just a reference, the actual fk column is in player not here!
    // However ai players are no visitors!
    private List<Player> players;

    @JsonIgnore
    // UsPl: a Visitor can start many Leagues
    // However bots cannot start a league
    @OneToMany(mappedBy = "visitor", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in league not here!
    private List<League> leagues;

    public Visitor() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        this.week = localDateAndTime.format(week);
        this.day = localDateAndTime.getDayOfMonth();
        this.visitorName = "visitorName";
    }

    public Visitor(String visitorName, int visitorNameSequence, String email) {
        this();
        this.visitorName = visitorName;
        this.visitorNameSequence = visitorNameSequence;
        this.email = email;
    }

    public boolean repayLoan() {
        int loan = this.securedLoan;
        int pay = this.balance;
        if (pay >= loan &
                loan > 0) {
            this.balance = pay - loan;
            this.securedLoan = 0;
            return true;
        }
        return false;

    }

    public boolean pawnShip(int pawnShipValue) {
        int loan = this.securedLoan;
        int pay = this.balance;
        if (loan > 0) {
            return false; // repay first
        }
        this.balance = pay + pawnShipValue;
        this.securedLoan = loan + pawnShipValue;

        return true;
    }

    public static int pawnShipValue(int max) {
        int seed = max <= 0 ? 1001 : max + 1;
        Random random = new Random();
        return random.nextInt(seed);
    }

    public String winCount(List<Player> playersPlayed) {
        int won = 0;
        for (Player player : playersPlayed) {
            if (player.isWinner()) {
                won++;
            }
        }
        return "won/total: [" + won + "/" + playersPlayed.size() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visitor visitor = (Visitor) o;
        return visitorId == visitor.visitorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorId);
    }

}


