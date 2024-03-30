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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;

import static java.time.temporal.TemporalAdjusters.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "leagues", indexes = {@Index(name = "leagues_index", columnList = "league_id",
        unique = true)})
public class League {

    @Id
    @GeneratedValue
    @Column(name = "league_id", nullable = false)
    private int leagueId;

    @JsonIgnore
    @Column(columnDefinition = "DATE")
    private LocalDate created;


    // Foreign keys

    // UsPl: a Visitor can start many Leagues
    // However bots cannot start a league
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "visitor_id", referencedColumnName = "visitor_id", foreignKey = @ForeignKey
            (name = "fk_visitor_id"), nullable = false)
    private Visitor visitor;


    // Normal fields

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "name_seq")
    private int nameSequence;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column(name = "is_active")
    private boolean active;

    @Column(columnDefinition = "DATE")
    private LocalDate ended;


    // References

    // L: A League can have more Games over time
    @OneToMany(mappedBy = "league", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<Game> games = new ArrayList<>();

    public League() {
        this.created = LocalDate.now();
        this.active = true;

    }

    public League(Visitor visitor, String name, int nameSequence) {
        this();
        this.visitor = visitor;
        this.name = name;
        this.nameSequence = nameSequence;

        endLeagueThisMonth(); //default
    }

    public boolean endLeagueDaysFromNow(int days) {
        if (!this.isActive()) return false;
        this.ended = LocalDate.now().plus(Period.ofDays(days));
        return true;
    }

    public boolean endLeagueNextMonday() {
        if (!this.isActive()) return false;
        this.ended = LocalDate.now().with(next(DayOfWeek.MONDAY));
        return true;
    }

    public boolean endLeagueThisMonth() {
        if (!this.isActive()) return false;
        this.ended = LocalDate.now().with(lastDayOfMonth());
        return true;
    }

    public boolean isActive() {
        if (!this.active) return false; // visitor can set to inactive before enddate
        if (this.ended == null) return true;
        // check if ended has passed
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        int days = Period.between(yesterday, this.ended).getDays();
        return days <= 0; // todo test this
    }

    public void closeLeaguePerYesterday() {
        this.ended = LocalDate.now().plusDays(-1); // yesterday;
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return leagueId == league.leagueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueId);
    }
}

