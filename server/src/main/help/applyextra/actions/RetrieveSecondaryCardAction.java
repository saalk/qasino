package applyextra.actions;

import lombok.extern.slf4j.Slf4j;
import applyextra.configuration.Constants;
import applyextra.operations.event.PersistProcessSpecificValue;
import applyextra.operations.event.RetrieveProcessSpecificValueAction;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Class to retrieve the secondary account number that has been persisted
 */
@Component
@Lazy
@Slf4j
public class RetrieveSecondaryCardAction extends RetrieveProcessSpecificValueAction {

    public Boolean perform(PersistProcessSpecificValue.ProcessSpecificDataActionDTO dto) {
        log.info("Retrieving secondary account number");
        return super.perform(dto);
    }

    @Override
    protected String getKey() {
        return Constants.SECONDARY_ACCOUNT_NUMBER;
    }
}
