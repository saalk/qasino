package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.audit.AbstractCardsAuditEvent;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.audit.impl.WhichWay;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.StringUtils;
import applyextra.pega.api.common.PegaApiServiceClient;
import applyextra.pega.api.common.PegaApiServiceException;
import applyextra.pega.api.model.salaryslip.SalarySlipData;
import nl.ing.riaf.core.event.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Lazy
@Component
@Slf4j
public class SalarySlipAction implements Action<SalarySlipAction.PegaSalarySlipActionDTO, EventOutput.Result> {

    private static final String REQUEST_ID = "RequestId";
    private static final String PEGA_ACTION = "PegaAction";
    private static final String SALARY_SLIP_ACTION = "SalarySlipAction";
    private static final String DOSSIER_ID = "DossierId";
    private static final String CASE_ID = "Created CaseId";
    private static final String EMPTY = "Empty";
    private static final String SALARY_SLIP_DATA = "SalarySlipData";

    @Resource
    private PegaApiServiceClient pegaApiServiceClient;
    @Resource
    private AuditDelegate auditDelegate;

    @Override
    public EventOutput.Result perform(final PegaSalarySlipActionDTO pegaSalarySlipActionDTO) {
        try {
            pegaSalarySlipActionDTO
                    .getSalarySlipData()
                    .setProcessType(pegaSalarySlipActionDTO.getContextType().getPegaProcessName());

            logMessage(pegaSalarySlipActionDTO.getRequestId(), pegaSalarySlipActionDTO.getSalarySlipData(), WhichWay.OUT);
            pegaApiServiceClient.execute(pegaSalarySlipActionDTO.getSalarySlipData());
            logMessage(pegaSalarySlipActionDTO.getRequestId(), pegaSalarySlipActionDTO.getSalarySlipData(), WhichWay.IN);
            if (StringUtils.isEmpty(pegaSalarySlipActionDTO.getSalarySlipData().getCaseId())){
                log.error("Pega case id is missing in response for requestId={}",pegaSalarySlipActionDTO.getRequestId());
                return EventOutput.Result.FAILURE;
            }
            return EventOutput.Result.SUCCESS;
        } catch (PegaApiServiceException e) {
            log.error("Failed to start a pega case", e);
            return EventOutput.Result.FAILURE;
        }
    }

    public interface PegaSalarySlipActionDTO {

        RequestType getContextType();
        SalarySlipData getSalarySlipData();
        String getRequestId();

    }


    private void logMessage(final String requestId,
                            final SalarySlipData salarySlipData,
                            final WhichWay whichWay) {
        auditDelegate.fireGenericEvent(new AbstractCardsAuditEvent(ApplicationEvent.Severity.LOW, whichWay) {
            @Override
            public void getSpecificFields(Map<String, Object> fields) {
                fields.put(PEGA_ACTION, SALARY_SLIP_ACTION);
                fields.put(REQUEST_ID, requestId);
                if (salarySlipData != null) {
                    fields.put(DOSSIER_ID, salarySlipData.getDossierId());
                    if (salarySlipData.getCaseId() != null) {
                        fields.put(CASE_ID, salarySlipData.getCaseId());
                    }
                } else {
                    fields.put(SALARY_SLIP_DATA, EMPTY);
                }
            }
        });
    }
}
