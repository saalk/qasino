package applyextra.actions;

import lombok.extern.slf4j.Slf4j;
import applyextra.event.ApplyExtraCardRRBEvent;
import applyextra.model.ApplyExtraCardDTO;
import applyextra.commons.event.AbstractFlowDTO;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static applyextra.configuration.Constants.TEST;

@Slf4j
@Lazy
@Component
public class InformRRBWrapperAction<T extends ApplyExtraCardDTO> implements Action<T, EventOutput.Result> {

    @Resource
    private ApplyExtraCardRRBEvent applyExtraCardRRBEvent;

    @Resource
    private JNDIUtil jndiUtil;

    /**
     * Wrapper function around RRB. In local testing, the RRB Client cannot be instantiated, and for tests
     * in the ACCP environment, the data can be unstable. This wrapper ensures that the request doesn't fail
     * if there is data inconsistency. Be careful, this does not by definition mean that updates on RRB are
     * always successful.
     * @param flowDTO
     * @return
     */
    @Override
    public EventOutput.Result perform(T flowDTO) {

        try {
            // TODO: Check what happens when an Extra card is closed, and another is reopened on the same account.
            // Pricing code should be set, and is currently done by RRB. what info do they use to determine what pricingcode to set.
            applyExtraCardRRBEvent.perform((AbstractFlowDTO) flowDTO);
        } catch (Exception e) {
            log.error("Request {} failed to inform RRB. Exception: {},  Message: {}", flowDTO.getRequestId(), e.getClass().getSimpleName(), e.getMessage());
            final String jndiValue = jndiUtil.getJndiValueWithDefault("cell/legacyRoot/param/generieke-logfile-adapter-omgeving", "PROD");
            log.warn("Determining continuation for environment: " + jndiValue);
            if (!TEST.equalsIgnoreCase(jndiValue)) { throw e; }
        }

        return EventOutput.Result.SUCCESS;
    }
}
