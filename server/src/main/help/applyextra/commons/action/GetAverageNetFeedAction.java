package applyextra.commons.action;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import applyextra.operations.dto.ListAccountsDTO;
import applyextra.operations.model.SimplePaymentAccount;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.performancedatalegacy.listthreemonthlyincome.ListThreeMonthlyIncomeServiceOperationClient;
import nl.ing.sc.performancedatalegacy.listthreemonthlyincome.value.ListThreeMonthlyIncomeBusinessRequest;
import nl.ing.sc.performancedatalegacy.listthreemonthlyincome.value.ListThreeMonthlyIncomeBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * The logic for the calculation of average net feed is as follows :
 * If NF1, NF2 and NF3 are 0, then NF is 0 (x=0)
 * If NF1 and NF2 are 0 and NF3 is not, NF is 0 (x=1)
 * If NF1 and NF3 are 0 and NF2 is not, NF is 0 (x=1)
 * If NF2 and NF3 are 0 and NF1 is not, ànd INC1 is 0, NF is 0 (x=1)
 * If NF2 and NF3 are 0 and NF1 is not, and INC1 is not, NF is NF1 (exception case)
 * If NF1 is 0 and NF2 NF3 are not, NF is lowest(NF2/NF3)
 * If NF2 is 0 and NF1 NF3 are not, NF is lowest(NF1/NF3)
 * If NF3 is 0 and NF1 NF2 are not, NF is lowest(NF1/NF2)
 * If all NF are not zero, NF is median(NF1/NF2/NF3)
 */
@Lazy
@Component
public class GetAverageNetFeedAction implements Action<GetAverageNetFeedAction.GetAverageNetFeedActionDTO, Boolean> {
    private static final String SERVICE_NAME = "ListThreeMonthlyIncome";
    @Resource
    private ListThreeMonthlyIncomeServiceOperationClient serviceOperationClient;

    @Override
    public Boolean perform(GetAverageNetFeedActionDTO flowDto) {
        ListThreeMonthlyIncomeBusinessRequest businessRequest = new ListThreeMonthlyIncomeBusinessRequest();
        for (SimplePaymentAccount simplePaymentAccount: flowDto.getListAccountsDTO().getAccountList()){
            businessRequest.getAccounts().add(AccountUtils.ibanToBbanAsString(simplePaymentAccount.getAccountNumber()));
        }

        final ServiceOperationTask<ListThreeMonthlyIncomeBusinessResponse> response = serviceOperationClient.execute(businessRequest);
        if (response.getResult().isOk()){
            ListThreeMonthlyIncomeBusinessResponse businessResponse = response.getResponse();
            Double avgNetFeed = new Double(0);
            Double netFeed1 = businessResponse.getNetFeed1().doubleValue();
            Double netFeed2 = businessResponse.getNetFeed2().doubleValue();
            Double netFeed3 = businessResponse.getNetFeed3().doubleValue();
            //This value represents the total positive, or non-zero values
            int x = 0;
            if (netFeed1 > 0){
                x++;
            }
            if (netFeed2 > 0){
                x++;
            }
            if (netFeed3 > 0){
                x++;
            }
            //exception case first
            Double income1 = businessResponse.getIncome1().doubleValue();
            if ( x < 2 && (netFeed1 > 0) && (income1 > 0)){
                avgNetFeed = netFeed1;
            }
            // Lowest value if there are 2 netfeeds present
            else if ( x == 2 ){
                if (netFeed3 == 0){
                    if (netFeed1 < netFeed2){
                        avgNetFeed = netFeed1;
                    } else {
                        avgNetFeed = netFeed2;
                    }
                } else if (netFeed2 == 0){
                    if (netFeed1 < netFeed3){
                        avgNetFeed = netFeed1;
                    } else {
                        avgNetFeed = netFeed3;
                    }
                } else {
                    if (netFeed2 < netFeed3){
                        avgNetFeed = netFeed2;
                    } else {
                        avgNetFeed = netFeed3;
                    }
                }
            }
            //When all netfeeds are filled, determine the median
            else if ( x == 3 ) { // 2 1 3
                if (netFeed1 > netFeed2
                        && netFeed1 < netFeed3) {
                    avgNetFeed = netFeed1;
                } else if // 3 1 2
                        (netFeed1 > netFeed3
                                && netFeed1 < netFeed2) {
                    avgNetFeed = netFeed1;
                } else if // 1 2 3
                        (netFeed1 > netFeed2
                                && netFeed2< netFeed3) {
                    avgNetFeed = netFeed2;
                } else if // 3 2 1
                        (netFeed2 > netFeed1
                                && netFeed2 < netFeed3) {
                    avgNetFeed = netFeed2;
                } else {
                    avgNetFeed = netFeed3;
                }
            }
            // All other cases, netfeed is 0
            //Set the net feed to the result
            flowDto.setAvgNetFeed(avgNetFeed);
        } else {
            throw new ActivityException(SERVICE_NAME, response.getResult()
                    .getError().getErrorCode(),
                    "Unable to get avgNetFeed from ListThreeMonthlyIncome",null);
        }
        return true;
    }

    public interface GetAverageNetFeedActionDTO {
        ListAccountsDTO getListAccountsDTO();

        void setAvgNetFeed(Double avgNetFeed);
    }
}
