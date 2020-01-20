package cloud.qasino.card.entity;

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
import java.time.format.DateTimeFormatter;
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

    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    // UsPl: a User can start many Leagues
    // However bots cannot start a league
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey
            (name = "fk_user_id"), nullable = false)
    private User user;

    // Normal fields

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "name_seq")
    private int nameSequence;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column(name = "is_active")
    private boolean active;

    @Column(name = "enddate", length = 25)
    private String endDate;

    // References
    @JsonIgnore
    // L: A League can have more Games over time
    @OneToMany(mappedBy = "league", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<Game> games = new ArrayList<>();

    public League() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.active = true;

    }

    public League(User user, String name, int nameSequence) {
        this();
        this.user = user;
        this.name = name;
        this.nameSequence = nameSequence;

        endLeagueThisMonth(); //default
    }

    public boolean endLeagueDaysFromNow(int days) {

        if (!this.isActive()) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate = LocalDate.now()
                .plus(Period.ofDays(days));
        this.endDate = endDate.format(formatter);

        return true;
    }

    public boolean endLeagueNextMonday(int days) {

        if (!this.isActive()) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate enddate = LocalDate.now();
        LocalDate nextMonday = enddate.with(next(DayOfWeek.MONDAY));
        this.endDate = nextMonday.format(formatter);
        return true;
    }

    public boolean endLeagueThisMonth() {

        if (!this.isActive()) return false;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate enddate = LocalDate.now();
        LocalDate lastDayOfMonth = enddate.with(lastDayOfMonth());
        this.endDate = lastDayOfMonth.format(formatter);
        return true;

    }

    public boolean isActive() {
        if (!this.active) return false; // user can set to inactive before enddate
        if (this.endDate == null) return true;

        // check if ended has passed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate enddate = LocalDate.parse(this.endDate, formatter);
        LocalDate yesterday = LocalDate.now().plusDays(-1);

        int days = Period.between(yesterday, enddate).getDays();
        return days <= 0; // todo test this
    }

    public void closeLeaguePerYesterday() {
        LocalDate localDate = LocalDate.now().plusDays(-1); // yesterday
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String result = localDate.format(formatter);
        this.endDate = result;
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

