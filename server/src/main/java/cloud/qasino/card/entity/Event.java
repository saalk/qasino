package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.playingcard.Location;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "events", indexes =
        {@Index(name = "events_game_index", columnList = "game_id", unique = false),
                @Index(name = "events_index", columnList = "event_id", unique = true)}
)
public class Event {

    @Id
    @GeneratedValue
    @Column(name = "event_id")
    private int eventId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    @Column(name = "game_id", nullable = false)
    private int gameId;

    @Column(name = "player_id", nullable = true)
    private int playerId;

    @Column(name = "card_id", nullable = true)
    private String cardId;


    // json
    @Column(name = "event_details", nullable = false)
    private String eventDetails;

    // Normal fields

    @Column(name = "player_order", nullable = false)
    private int playerOrder;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "move_number", nullable = false)
    private int moveNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    @Column(name = "bet", nullable = false)
    private int bet;

    // References

    public Event() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }

    public Event(Game game, Player player, String cardId) {
        this();
        this.gameId = game.getGameId();
        this.playerId = player.getPlayerId();
        this.cardId = cardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId == event.eventId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", created='" + created + '\'' +
                ", gameId=" + gameId +
                ", playerId=" + playerId +
                ", cardId='" + cardId + '\'' +
                ", eventDetails='" + eventDetails + '\'' +
                ", playerOrder=" + playerOrder +
                ", roundNumber=" + roundNumber +
                ", moveNumber=" + moveNumber +
                ", location=" + location +
                ", bet=" + bet +
                '}';
    }
}
