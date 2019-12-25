package cloud.qasino.card.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Table(name = "users", indexes = {@Index(name = "users_index", columnList = "userId")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int userId;

    @Column(length = 25)
    private String created;

    @Column(length = 50, nullable = false)
    private String alias;

    @Column(length = 50)
    private String email;

    private int cubits;

    private int securedLoan;

    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the parent Player: delete Game when Player is deleted (no other actions!)
    //@JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "users_id", referencedColumnName = "users_id", foreignKey = @ForeignKey(name = "users_id"))
    ////@JsonProperty("players")
    @JsonIgnore
    private List<Player> players;

    public User() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }

    public User(String alias) {
        this();
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", created='" + created + '\'' +
                ", alias='" + alias + '\'' +
                ", email='" + email + '\'' +
                ", cubits=" + cubits +
                ", securedLoan=" + securedLoan +
                '}';
    }
}


