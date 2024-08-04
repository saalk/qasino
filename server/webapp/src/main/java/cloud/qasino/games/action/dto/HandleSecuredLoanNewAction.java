package cloud.qasino.games.action.dto;

import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class HandleSecuredLoanNewAction extends ActionDto<EventOutput.Result> {

    // formatter:off
    @Resource
    VisitorAndLeaguesService visitorAndLeaguesService;
    private int securedLoan;
    private int balance;
    // formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (qasino.getVisitor() == null) {
            qasino.getMessage().setErrorKey("visitorId");
            qasino.getMessage().setNotFoundErrorMessage(null);
            return EventOutput.Result.FAILURE;
        }
        if (qasino.getParams().getSuppliedQasinoEvent() != QasinoEvent.PAWN &&
                qasino.getParams().getSuppliedQasinoEvent() != QasinoEvent.REPAY) {
            qasino.getMessage().setErrorKey("pawn or repay");
            qasino.getMessage().setBadRequestErrorMessage("not supplied");
            return EventOutput.Result.FAILURE;
        }

        securedLoan = qasino.getVisitor().getSecuredLoan();
        balance = qasino.getVisitor().getBalance();

        if (qasino.getParams().getSuppliedQasinoEvent() == QasinoEvent.REPAY) {
            boolean repayOk = repayLoan();
            if (!repayOk) {
                setConflictErrorMessage(qasino, "Repay", "Repay loan with balance not possible, balance too low");
                return EventOutput.Result.FAILURE;
            }

        } else {
            boolean pawnOk = pawnShip(0);
            if (!pawnOk) {
                setConflictErrorMessage(qasino, "Pawn", "Ship already pawned, repay first");
                return EventOutput.Result.FAILURE;
            }
        }
        qasino.getVisitor().setSecuredLoan(securedLoan);
        qasino.getVisitor().setBalance(balance);
        qasino.setVisitor(visitorAndLeaguesService.repayOrPawn(qasino.getVisitor()));
        return EventOutput.Result.SUCCESS;
    }

    // formatter:off
    private void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("Action [" + id + "] invalid");
    }

    private void setBadRequestErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setBadRequestErrorMessage("Action [" + id + "] invalid");
    }

    public boolean repayLoan() {
        if (balance >= securedLoan) {
            balance = balance - securedLoan;
            securedLoan = 0;
            return true;
        }
        return false; // not enough balance to repay all
    }

    public boolean pawnShip(int max) {
        int seed = max <= 0 ? 1001 : max + 1;
        Random random = new Random();
        int pawnShipValue = random.nextInt(seed);
        if (securedLoan > 0) {
            return false; // repay first
        }
        balance = balance + pawnShipValue;
        securedLoan = pawnShipValue;
        return true;
    }

}
