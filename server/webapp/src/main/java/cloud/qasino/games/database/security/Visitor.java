package cloud.qasino.games.database.security;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

@Entity
 @DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
@NoArgsConstructor
@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "visitor", indexes =
        {@Index(name = "visitors_index", columnList = "visitor_id", unique = true),
                @Index(name = "alias_index", columnList = "alias", unique = false),
                @Index(name = "username_index", columnList = "username", unique = true)
        })
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visitor_id", nullable = false)
    private long visitorId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    @Column(name = "username", length = 25, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 60)
    private String password;

    @JsonIgnore
    private boolean enabled;

    @JsonIgnore
    private boolean isUsing2FA;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "visitor_id", referencedColumnName = "visitor_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Collection<Role> roles;

    // Foreign keys


    @JsonProperty("aliasSequence")
    @Column(name = "alias_seq")
    private int aliasSequence;

    // Normal fields
    @JsonProperty("alias")
    @Column(name = "alias", length = 50, nullable = false)
    private String alias;

    @JsonProperty("email")
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

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    @Column(name = "weekday", length = 2)
    private int weekday;

    // References

    // PL: a Visitor becomes a Player when playing a GameSubTotals
    @JsonIgnore
//    @OneToMany(mappedBy = "visitor", cascade = CascadeType.REMOVE)
    @OneToMany(mappedBy = "visitor") // no cascade otherwise league.visitor is set to null !!
    // just a reference, the actual fk column is in player not here!
    // However ai players are no visitors!
    private List<Player> players;

    public void addPlayer(Player player) {
        player.setVisitor(this);
        List<Player> found = players.stream().filter(vis -> vis.getVisitor().getVisitorId() == this.getVisitorId()).toList();
        if (found.isEmpty()) players.add(player);
    }

    @JsonIgnore
    // UsPl: a Visitor can start many Leagues
    // However bots cannot start a league
//    @OneToMany(mappedBy = "visitor", cascade = CascadeType.REMOVE)
    @OneToMany(mappedBy = "visitor") // no cascade otherwise league.visitor is set to null !!
    // just a reference, the actual fk column is in league not here!
    private List<League> leagues;

    public void addLeague(League league) {
        league.setVisitor(this);
        List<League> found = leagues.stream().filter(vis -> vis.getVisitor().getVisitorId() == this.getVisitorId()).toList();
        if (found.isEmpty()) leagues.add(league);
    }

    private Visitor(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.roles = builder.roles;

        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        this.week = localDateAndTime.format(week);
        this.weekday = localDateAndTime.getDayOfMonth();

        this.alias = builder.alias;
        this.aliasSequence = builder.aliasSequence;
        this.email = builder.email;
    }

    public static Visitor buildDummy(String username, String alias) {
        if (username.isEmpty()) username = "username";
        if (alias.isEmpty()) username = "Alias";
        return new Builder()
                .withAlias(alias)
                .withAliasSequence(1)
                .withBalance(0)
                .withEmail("email@acme.com")
                .withPassword("password")
                .withRoles(Collections.singleton(new Role("ROLE_USER")))
                .withSecuredLoan(0)
                .withUsername(username)
                .build();
    }

    public static class Builder {
        private String username;
        private String password;
        private Set<Role> roles;

        private String alias;
        private int aliasSequence;
        private String email;

        private int balance;
        private int securedLoan;

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withRoles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder withAliasSequence(int aliasSequence) {
            this.aliasSequence = aliasSequence;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withBalance(int balance) {
            this.balance = balance;
            return this;
        }

        public Builder withSecuredLoan(int securedLoan) {
            this.securedLoan = securedLoan;
            return this;
        }

        public Visitor build() {
            return new Visitor(this);
        }
    }

    public boolean repayLoan() {
        if (this.securedLoan <= 0) return true; // nothing to repay
        if (this.balance >= this.securedLoan) {
            this.balance = this.balance - this.securedLoan;
            this.securedLoan = 0;
            return true;
        }
        return false; // not enough balance to repay all
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visitor visitor = (Visitor) o;
        return visitorId == visitor.visitorId;
    }

    @Override
    public String toString() {
        String role = this.roles==null?"null": String.valueOf(this.roles.stream().findFirst());
        return "(" +
                "visitorId=" + this.visitorId +
                ", securedLoan=" + this.securedLoan +
                ", balance=" + this.balance +
                ", email=" + this.email +
                ", alias=" + this.alias +
                ", aliasSequence=" + this.aliasSequence +
                ", username=" + this.alias +
                ", roles=" + role +
                ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitorId);
    }
}


