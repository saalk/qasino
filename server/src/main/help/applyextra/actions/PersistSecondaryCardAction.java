package applyextra.actions;

import lombok.extern.slf4j.Slf4j;
import applyextra.configuration.Constants;
import applyextra.operations.event.PersistProcessSpecificValue;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**Class to persist the secondary account number.
 *
 */
@Component
@Lazy
@Slf4j
public class PersistSecondaryCardAction extends PersistProcessSpecificValue {

    public Boolean perform(ProcessSpecificDataActionDTO dto) {
        log.info("Storing the secondaryAccountNumber");
        return super.perform(dto);
    }

    @Override
    protected String getKey() {
        return Constants.SECONDARY_ACCOUNT_NUMBER;
    }

}
