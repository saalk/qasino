package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import nl.ing.api.party.domain.Arrangement;
import nl.ing.api.party.domain.ArrangementKey;
import applyextra.api.arrangement.ArrangementSearchResourceClient;
import applyextra.api.arrangement.value.ArrangementSearchBusinessRequest;
import applyextra.api.arrangement.value.ArrangementSearchBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Component
@Slf4j
@SuppressWarnings("rawtypes")
public class ArrangementKeySearchEvent extends AbstractEvent {

    @Resource
    private ArrangementSearchResourceClient resourceClient;

    @Override
    public EventOutput execution(final Object... eventOutput) {
        final ArrangementKeySearchEventDTO flowDTO = (ArrangementKeySearchEventDTO) eventOutput[0];
        ArrangementSearchBusinessResponse arrangementSearchBusinessResponse = null;
        try {
            arrangementSearchBusinessResponse = resourceClient.execute(flowDTO.getArrangementSearchBusinessRequest());
        } catch (final Exception exception) {
            log.error("Exception while executing ArrangementKeySearchEvent: ", exception);
            throw new ActivityException("ArrangementKeySearchEvent", "call to ArrangementResourceClient failed", exception);
        }

        flowDTO.setRequiredArrangementKey(extractArrangementKey(arrangementSearchBusinessResponse, flowDTO.getSearchArrangementKeyTypeCode()));

        return EventOutput.success();
    }

    private ArrangementKey extractArrangementKey(final ArrangementSearchBusinessResponse arrangementSearchBusinessResponse, final String typeCode) {
        if (arrangementExists(arrangementSearchBusinessResponse)) {
            final Arrangement arrangement = arrangementSearchBusinessResponse.getMainAccountArrangements()
                    .get(0);
            if (arrangementKeyExists(arrangement)) {
                for (final ArrangementKey arrangementKey :  arrangement.getArrangementKeys()) {
                    if (StringUtils.equals(arrangementKey.getTypeCode(), typeCode)) {
                        return arrangementKey;
                    }
                }
            }
        }
        return null;
    }

    private boolean arrangementExists(final ArrangementSearchBusinessResponse arrangementSearchBusinessResponse) {
        return arrangementSearchBusinessResponse.getMainAccountArrangements() != null
                && !arrangementSearchBusinessResponse.getMainAccountArrangements().isEmpty();
    }

    private boolean arrangementKeyExists(final Arrangement arrangement) {
        return arrangement.getArrangementKeys() != null && !arrangement.getArrangementKeys().isEmpty();
    }

    public interface ArrangementKeySearchEventDTO {
        ArrangementSearchBusinessRequest getArrangementSearchBusinessRequest();
        String getSearchArrangementKeyTypeCode();
        void setRequiredArrangementKey(final ArrangementKey arrangementKey);
        ArrangementKey getRequiredArrangementKey();
    }

}
