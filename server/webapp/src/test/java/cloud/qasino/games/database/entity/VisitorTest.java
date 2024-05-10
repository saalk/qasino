package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorTest extends QasinoSimulator{

    @Test
    public void givenQasinoVisitor_whenCreated_thenReturnValidObjectValues() {

        assertThat(visitor.getUsername()).isEqualTo("Julie");
        assertThat(visitor.getEmail()).isEqualTo("Julie@domain.com");
        assertThat(visitor.getBalance()).isEqualTo(0);
        assertThat(visitor.getAliasSequence()).isEqualTo(1);

        // changes
        visitor.setAlias("John");
        assertThat(visitor.getAlias()).isEqualTo("John");
        visitor.setAlias("Julie");
        assertThat(visitor.getAlias()).isEqualTo("Julie");
        // TODO increment sequence
//        assertThat(visitor.getVisitorNameSequence()).isEqualTo(1);
        visitor.setEmail("john@domain.com");
        assertThat(visitor.getEmail()).isEqualTo("john@domain.com");

        // start with balance 10
        int startBalance = 10;
        visitor.setBalance(10);

        // pawn the ship
        int pawnValue = Visitor.pawnShipValue(1000);
        visitor.pawnShip(pawnValue);
        assertThat(visitor.getSecuredLoan()).isEqualTo(pawnValue);
        assertThat(visitor.getBalance()).isEqualTo(pawnValue+startBalance);

        // repay the loan
        visitor.repayLoan();
        assertThat(visitor.getSecuredLoan()).isEqualTo(0);
        assertThat(visitor.getBalance()).isEqualTo(startBalance);

        // cleanup for next tests and reset the defaults
        pawnValue = Visitor.pawnShipValue(1000);
        visitor.pawnShip(pawnValue);
        visitor.setBalance(visitor.getBalance()-10);

    }

}
