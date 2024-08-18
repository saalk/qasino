package cloud.qasino.games.action.visitor;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.database.service.VisitorService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class HandleSecuredLoanAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Resource
    VisitorService visitorService;
    private int securedLoan;
    private int balance;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getVisitor() == null) {
            qasino.getMessage().setNotFoundErrorMessage("visitorId","null",null);
            return EventOutput.Result.FAILURE;
        }
        if (qasino.getParams().getSuppliedQasinoEvent() != QasinoEvent.PAWN &&
                qasino.getParams().getSuppliedQasinoEvent() != QasinoEvent.REPAY) {
            qasino.getMessage().setBadRequestErrorMessage("QasinoEvent","pawn or repay","not supplied");
            return EventOutput.Result.FAILURE;
        }

        this.securedLoan = qasino.getVisitor().getSecuredLoan();
        this.balance = qasino.getVisitor().getBalance();

        if (qasino.getParams().getSuppliedQasinoEvent() == QasinoEvent.REPAY) {
            boolean repayOk = repayLoan();
            if (!repayOk) {
                qasino.getMessage().setConflictErrorMessage("Repay","Repay loan with balance not possible, balance too low","Action [Repay] invalid");
                return EventOutput.Result.FAILURE;
            }

        } else {
            boolean pawnOk = pawnShip(0);
            if (!pawnOk) {
                qasino.getMessage().setConflictErrorMessage("Pawn","Ship already pawned, repay first","Action [Pawn] invalid");
                return EventOutput.Result.FAILURE;
            }
        }

        qasino.setVisitor(visitorService.repayOrPawn(qasino.getParams(), this.balance, this.securedLoan));
        return EventOutput.Result.SUCCESS;
    }

    // @formatter:on
    public boolean repayLoan() {
        if (this.balance >= this.securedLoan) {
            this.balance = this.balance - this.securedLoan;
            this.securedLoan = 0;
            return true;
        }
        return false; // not enough balance to repay all
    }
    public boolean pawnShip(int max) {
        int seed = max <= 0 ? 1001 : max + 1;
        Random random = new Random();
        int pawnShipValue = random.nextInt(seed);
        if (this.securedLoan > 0) {
            return false; // repay first
        }
        this.balance = this.balance + pawnShipValue;
        this.securedLoan = pawnShipValue;
        return true;
    }

}
