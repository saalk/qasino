package cloud.qasino.quiz.entity;

import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.Player;
import cloud.qasino.quiz.entity.User;
import cloud.qasino.quiz.entity.enums.game.Type;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "results", indexes = {@Index(name = "results_index", columnList = "result_id",
        unique = true)})
public class Result {

    @Id
    @GeneratedValue
    @Column(name = "result_id", nullable = false)
    private int resultId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    // UsPl: a Result has one PLayer that wins the GameSubTotals
    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey = @ForeignKey
            (name = "fk_player_id"), nullable = false)
    private cloud.qasino.quiz.entity.Player player;

    // UsPl: a User can win the Games as a Player
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey
            (name = "fk_user_id"), nullable = true)
    private cloud.qasino.quiz.entity.User user;

    // the game for which the result is achieved
    @OneToOne (cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id",foreignKey = @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private cloud.qasino.quiz.entity.Game game;


    // Normal fields
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Column(name = "year", length = 4)
    private int year;

    @Column(name = "month", length = 20)
    private Month month;

    @Column(name = "week", length = 3)
    private String week;

    @Column(name = "day", length = 2)
    private int day;

    @Setter(AccessLevel.NONE)
    @Column(name = "is_active")
    private int fichesWon;

    // References

    public Result() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        this.week = localDateAndTime.format(week);
        this.day = localDateAndTime.getDayOfMonth();
    }

    public Result(Player player, User user, Game game, Type type, int fichesWon) {
        this();
        this.player = player;
        this.user = user;
        this.game = game;

        this.type = type;
        this.fichesWon = fichesWon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return resultId == result.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }

}

