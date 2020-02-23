package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.currentaccounts.CurrentAccountInquiryResourceClient;
import applyextra.api.currentaccounts.value.CurrentAccountInquiryBusinessResponse;
import applyextra.api.currentaccounts.value.PaymentAccount;
import applyextra.api.exception.ResourceException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.dto.ListAccountsDTO;
import applyextra.operations.model.SimplePaymentAccount;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
@Slf4j
public class ListAccountsAction implements Action<ListAccountsAction.ListAccountsActionDTO, EventOutput> {
    @Resource
    private CurrentAccountInquiryResourceClient resourceClient;
    
    @Override
    public EventOutput perform(final ListAccountsActionDTO dto) {
        ListAccountsDTO listAccountsDTO = dto.getListAccountsDTO();
        if (listAccountsDTO == null) {
            listAccountsDTO = new ListAccountsDTO();
            dto.setListAccountsDTO(listAccountsDTO);
        }
        List<SimplePaymentAccount> listAccounts = getOwnerAccounts(dto.getChannelContext());
        final EventOutput result = new EventOutput(EventOutput.Result.SUCCESS);
        listAccountsDTO.getAccountList().addAll(listAccounts);
        listAccountsDTO.setListAccountResultOk(result.isSuccess());
        return result;
    }

    private List<SimplePaymentAccount> getOwnerAccounts(ChannelContext channelContext) {
    	CurrentAccountInquiryBusinessResponse businessResponse;
    	try {
			businessResponse = resourceClient.execute(channelContext);
		} catch (ResourceException e) {
			log.error("Unexpected exception while calling gCurrAcctInqAPI : " + e.getMessage());
            throw new WebApplicationException(e, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
		}

    	List<PaymentAccount> paymentAccounts = businessResponse.getPaymentAccountsWherePartyIdIsOwner();
    	List<SimplePaymentAccount> simplePaymentAccounts = new ArrayList<>();

        SimplePaymentAccount accountOutput;
        for (PaymentAccount paymentAccount : paymentAccounts) {
            accountOutput = new SimplePaymentAccount(paymentAccount, paymentAccount.getEncryptedAccountNumber());
            simplePaymentAccounts.add(accountOutput);
        }
        return simplePaymentAccounts;

    }

    public interface ListAccountsActionDTO {
        ChannelContext getChannelContext();

        void setListAccountsDTO(ListAccountsDTO accountDTO);

        ListAccountsDTO getListAccountsDTO();
    }

}
