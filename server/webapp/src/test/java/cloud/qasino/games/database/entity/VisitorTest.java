package cloud.qasino.games.database.entity;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorTest extends QasinoSimulator {

    @Test
    public void givenQasinoVisitor_whenCreated_thenReturnValidObjectValues() {

        assertThat(visitor.getUsername()).isEqualTo("username");
        assertThat(visitor.getEmail()).isEqualTo("email@acme.com");
        assertThat(visitor.getBalance()).isNotEqualTo(0);
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

       // pawn the ship
        assertThat(visitor.getSecuredLoan()).isEqualTo(visitor.getBalance());

        // repay the loan
        visitor.repayLoan();
        assertThat(visitor.getSecuredLoan()).isEqualTo(0);
        assertThat(visitor.getBalance()).isEqualTo(0);


    }

}
