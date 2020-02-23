package applyextra.commons.action;

import applyextra.commons.dao.request.ChangeProcessService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.ChangeProcessEntity;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.core.util.JNDIUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

import static applyextra.commons.model.database.entity.ChangeProcessEntity.ProcessName.*;

@Component
@Lazy
public class GatherChangeProcessDataAction implements Action<GatherChangeProcessDataAction.GatherChangeProcessDataActionDTO, EventOutput.Result> {

    @Resource
    private ChangeProcessService service;

    @Resource
    private JNDIUtil jndiUtil;

    private static final int DEFAULT_DAYS = 3;

    private int numberOfDaysToCheck;


    @PostConstruct
    public void init() {
        numberOfDaysToCheck = jndiUtil.getJndiValueWithDefault("param/changeProcess/numberOfDaysToStopOtherProcesses", DEFAULT_DAYS);
    }

    @Override
    public EventOutput.Result perform(GatherChangeProcessDataActionDTO dto) {
        if(!dto.getChangeProcessHistoricalRequests().isEmpty()) {
            dto.getChangeProcessHistoricalRequests().clear();
        }
        final String customerId = dto.getCustomerId();
        final String beneficiaryId = dto.getBeneficiaryId();
        final String iban = dto.getIban();

        if(StringUtils.isNotBlank(customerId))
            dto.getChangeProcessHistoricalRequests().addAll(service.getChangeProcessHistoricalRequests(customerId, CHANGE_ADDRESS, numberOfDaysToCheck));
        if(StringUtils.isNotBlank(beneficiaryId))
            dto.getChangeProcessHistoricalRequests().addAll(service.getChangeProcessHistoricalRequests(beneficiaryId, CHANGE_NAME, numberOfDaysToCheck));
        if(StringUtils.isNotBlank(iban))
            dto.getChangeProcessHistoricalRequests().addAll(service.getChangeProcessHistoricalRequests(iban, CHANGE_PRICING, numberOfDaysToCheck));

        return EventOutput.Result.SUCCESS;
    }

    public interface GatherChangeProcessDataActionDTO {
        List<ChangeProcessEntity> getChangeProcessHistoricalRequests();
        String getCustomerId();
        String getBeneficiaryId();
        String getIban();
    }
}
