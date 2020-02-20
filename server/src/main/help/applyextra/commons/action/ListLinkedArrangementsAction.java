package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.audit.impl.AuditDelegateHelper;
import applyextra.commons.helper.GraphiteHelper;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.ConstantsUtil;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listlinkedarrangements2.ListLinkedArrangementsServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listlinkedarrangements2.value.ArrangementKey;
import nl.ing.sc.arrangementretrieval.listlinkedarrangements2.value.ArrangementRelation;
import nl.ing.sc.arrangementretrieval.listlinkedarrangements2.value.ListLinkedArrangementsBusinessRequest;
import nl.ing.sc.arrangementretrieval.listlinkedarrangements2.value.ListLinkedArrangementsBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Praveena Biyyam on 22-6-2018
 */
@Slf4j
@Component
@Lazy
public class ListLinkedArrangementsAction implements Action<ListLinkedArrangementsAction.ListLinkedArrangementsDTO, Boolean> {

    @Resource
    ListLinkedArrangementsServiceOperationClient listLinkedArrangementsServiceOperationClient;
    @Resource
    private GraphiteHelper graphiteHelper;
    @Resource
    private AuditDelegateHelper auditDelegateHelper;

    @Override
    public Boolean perform(ListLinkedArrangementsDTO flowDTO) {
        ServiceOperationTask<ListLinkedArrangementsBusinessResponse> response;
        ListLinkedArrangementsBusinessRequest listLinkedArrangementsRequest = transformDTOtoBusinessRequest(flowDTO.getArrangementKeyList());
        response =  this.listLinkedArrangementsServiceOperationClient.execute(listLinkedArrangementsRequest);
        if (response.getResult().isOk()) {
            log.debug("looking for listLinkedArrangements response size "+response.getResponse().getArrangementRelationList().size()+" for the messageId=\""+flowDTO.getMessageId()+"\"");
            flowDTO.getLinkedArrangementRelationList().addAll(response.getResponse().getArrangementRelationList());
            auditDelegateHelper.logMessage(ConstantsUtil.LIST_LINKED_ARRANGEMENTS_SERVICE, "Move successful for the messageId=\""+flowDTO.getMessageId()+"\"", flowDTO.getMessageId());
            graphiteHelper.customCounter(ConstantsUtil.LIST_LINKED_ARRANGEMENTS_COUNTER, ConstantsUtil.SUCCESS_REQUESTS_COUNTER, 1 );
        } else if (response.getResponse() != null && !(response.getResponse()).getArrangementRelationList().isEmpty()) {
            graphiteHelper.customCounter(ConstantsUtil.LIST_LINKED_ARRANGEMENTS_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1 );
            throw new ActivityException(ConstantsUtil.LIST_LINKED_ARRANGEMENTS_SERVICE, response.getResult().getError().getErrorCode(), "Cannot fetch arrangement list for the messageId=\""+flowDTO.getMessageId()+"\"", null);
        } else {
            graphiteHelper.customCounter(ConstantsUtil.LIST_LINKED_ARRANGEMENTS_COUNTER, ConstantsUtil.FAILURE_REQUESTS_COUNTER, 1 );
            throw new ActivityException("ListLinkedArrangements", "Cannot fetch arrangement list for the messageId=\""+flowDTO.getMessageId()+"\"", null);
        }
        return true;
    }

    private ListLinkedArrangementsBusinessRequest transformDTOtoBusinessRequest(List<ArrangementKey> arrangementKeyList) {
        ListLinkedArrangementsBusinessRequest request = new ListLinkedArrangementsBusinessRequest();
        request.getArrangementKeyList().add(arrangementKeyList.get(0));
        return request;
    }

    public interface ListLinkedArrangementsDTO {
        String getMessageId();
        List<ArrangementKey> getArrangementKeyList();
        List<ArrangementRelation> getLinkedArrangementRelationList();
    }
}
