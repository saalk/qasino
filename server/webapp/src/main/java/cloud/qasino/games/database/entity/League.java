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
import java.time.LocalDateTime;
import java.time.Period;

import static java.time.temporal.TemporalAdjusters.*;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "league", indexes = {
        // not needed : @Index(name = "leagues_index", columnList = "league_id", unique = true)
})
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "league_id")
    private long leagueId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


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

    @Getter(AccessLevel.NONE)
    @Column(name = "is_active")
    private boolean active;

    @JsonIgnore
    @Column(name = "ended", length = 25)
    private String ended;


    // References

    // L: A League can have more Games over time
    @OneToMany(mappedBy = "league", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<Game> games = new ArrayList<>();

    public League() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);

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
        LocalDateTime localDateAndTime = LocalDateTime.now().plus(Period.ofDays(days));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);

        return true;
    }

    public boolean endLeagueNextMonday() {
        if (!this.isActive()) return false;
        LocalDateTime localDateAndTime = LocalDateTime.now().with(next(DayOfWeek.MONDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);

        return true;
    }

    public boolean endLeagueThisMonth() {
        if (!this.isActive()) return false;
        LocalDateTime localDateAndTime = LocalDateTime.now().with(lastDayOfMonth());;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);

        return true;
    }

    public boolean isActive() {
        if (!this.active) return false; // \"visitor\"can set to inactive before enddate

        if (this.ended == null || this.ended.isEmpty()) return true;

        // check if ended has passed
        LocalDate yesterday = LocalDate.now().plusDays(-1);

        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        LocalDate ended = LocalDate.parse(this.ended,dateTimeFormatter);

        int days = Period.between(yesterday, ended).getDays();
        return days > 0;
    }

    public void closeLeaguePerYesterday() {

        LocalDateTime localDateAndTime = LocalDateTime.now().plusDays(-1); // yesterday;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);
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

