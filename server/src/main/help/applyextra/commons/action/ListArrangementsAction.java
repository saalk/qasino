package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.model.ArrangementStatus;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listarrangements2.ListArrangementsServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessRequest;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessResponse;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * New suppliedMove to make a tailored call to list arrangements.
 * The arrangements can be searched by category codes, arrangement status. If there is a next,
 * then the next can also be searched.
 * Please check the comments for the default methods below
 */
@Component
@Lazy
@Slf4j
public class ListArrangementsAction implements Action<ListArrangementsAction.ListArrangementsActionDTO, EventOutput.Result> {

    private static final int MAX_NEXT_COUNT = 20;

    @Resource
    private ListArrangementsServiceOperationClient resourceClient;

    @Override
    public EventOutput.Result perform(ListArrangementsActionDTO dto) {

        int numberOfPages = 0;

        String partyID = dto.getCustomerId();

        //Please check the comments below for the arrangement status and categorycodes
        ListArrangementsBusinessRequest request = new ListArrangementsBusinessRequest(partyID, ListArrangementsBusinessRequest.PartyType.PRIVATE, dto.getArrangementStatus());

        //If CategoryCodes is set, then populate the biz request with the categorycodes
        if (!dto.getCategoryCodes().isEmpty()) {
            request.getCategoryCodes().addAll(dto.getCategoryCodes());
        }
        ServiceOperationTask<ListArrangementsBusinessResponse> response = null;

        //Logic for calling list arrangements multiple times.
        do {
            if (canSetNext(response)) {
                request.setNext(response.getResponse().getNext());
            }

            try {
                response = resourceClient.execute(request);

                if (response.getResult().isOk()) {
                    dto.getRoleWithArrangements().addAll((response.getResponse()).getRoleWithArrangementList());

                } else {
                        throw new ActivityException("ListArrangements", response.getResult().getError().getErrorCode(),
                                "Cannot fetch role list from list arrangements", null);
                }

            } catch (Exception ex) {
                if (!dto.getRoleWithArrangements().isEmpty()) {
                    log.warn("Got an exception during call for NEXT page. Returning the arrangments obtained till now.");
                    return EventOutput.Result.SUCCESS;
                } else {
                    throw ex;
                }
            }

            //To avoid memory leak
            if (numberOfPages >= MAX_NEXT_COUNT) break;
            numberOfPages++;

        } while (canSetNext(response)); //Loop as long as there is a next in the response


        return EventOutput.Result.SUCCESS;
    }

    private boolean canSetNext(ServiceOperationTask<ListArrangementsBusinessResponse> response) {
        return response!=null && response.getResponse() != null && StringUtils.isNotEmpty(response.getResponse().getNext());
    }

    public interface ListArrangementsActionDTO {

        String getCustomerId();

        List<RoleWithArrangement> getRoleWithArrangements();

        /**
         * 1 is the default which returns active arrangments
         * Ensure that you return null if you want both closed and active arrangements.
         * 2 is for closed arrangements
         * @return
         */
        default Integer getArrangementStatus() {
            return ArrangementStatus.ACTIVE.getArrangementStatus();
        }

        /**
         * Ensure that you overrride this method to set the list of category codes that you need to filer
         * @return
         */
        default List<Integer> getCategoryCodes() {
            return new ArrayList();
        }

    }
}
