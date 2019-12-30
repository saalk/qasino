package cloud.qasino.card.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Entity
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

    @Column(name = "alias", length = 50, nullable = true)
    //@NotBlank(message = "Alias is mandatory")
    private String alias;

    @Column(name = "email", length = 50, nullable = true)
    //@NotBlank(message = "Email is mandatory")
    private String email;

    @Column(name = "cubit")
    private int fiches;

    @Column(name = "secured_loan")
    private int pawn;


    // References

    // PL: a User becomes a Player when playing a Game
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    // just a reference, the actual fk column is in player not here!
    private List<Player> players;

    public User() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }

    public User(String alias, String email) {
        this();
        this.alias = alias;
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", created='" + created + '\'' +
                ", alias='" + alias + '\'' +
                ", email='" + email + '\'' +
                ", fiches=" + fiches +
                ", pawn=" + pawn +
                '}';
    }

    public boolean repayLoan(){

        if (this.fiches >= this.pawn) {
            this.fiches = --this.pawn;
            this.pawn = 0;
            return true;
        }
        return false;
    }

    public boolean pawnShip(int max) {

        if (this.pawn == 0) {
            int seed = max == 0 ? 1001 : max + 1;
            Random random = new Random();
            this.pawn = random.nextInt(seed);
            this.fiches = ++ this.pawn;
            return true;
        }
        return false;
    }

    public String winCount(List<Game> gamesPlayed) {

        int won = 0;

        for (Game game : gamesPlayed) {
            if (game.getWinner().getPlayerId() == (this.getUserId())) {
                won++;
            }
        }

        return "won/total: [" + won + "/"+ gamesPlayed.size()+"]";
    }
}


