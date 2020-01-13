package cloud.qasino.card.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Entity
@DynamicUpdate
// INSERT INTO USERS USERS VALUES(1, 'alias','created','email',123,123)
@Data
@Table(name = "users", indexes =
        { @Index(name = "users_index", columnList = "user_id", unique = true),
          @Index(name = "alias_index", columnList = "alias", unique = false )
        })
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "created", length = 25)
    private String created;

    // Foreign keys

    // Normal fields

    @Column(name = "alias", length = 50, nullable = false)
    @NotBlank(message = "Alias is mandatory")
    private String alias;

    @Column(name = "alias_seq")
    private int aliasSequence;

    @Column(name = "email", length = 50, nullable = true)
    private String email;

    @Column(name = "fiches")
    private int fiches;

    @Column(name = "secured_loan")
    private int securedLoan;


    // References

    // PL: a User becomes a Player when playing a Game
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    // just a reference, the actual fk column is in player not here!
    // However ai players are no users!
    private List<Player> players;

    public User() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.alias = "alias";
    }

    public User(String alias, int aliasSequence, String email) {
        this();
        this.alias = alias;
        this.aliasSequence = aliasSequence;
        this.email = email;
    }

    public boolean repayLoan(){

        if (this.fiches >= this.securedLoan) {
            this.fiches = --this.securedLoan;
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
            this.fiches = ++ this.securedLoan;
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
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", created='" + created + '\'' +
                ", alias='" + alias + '\'' +
                ", aliasSequence=" + aliasSequence +
                ", email='" + email + '\'' +
                ", fiches=" + fiches +
                ", securedLoan=" + securedLoan +
                ", players=" + players +
                '}';
    }
}


