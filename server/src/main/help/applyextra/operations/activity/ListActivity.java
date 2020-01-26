package applyextra.operations.activity;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.model.CreditCard;
import applyextra.operations.converters.CreditcardServiceClientToCreditcardModelConverter;
import applyextra.operations.dto.PartyReferenceDTO;
import nl.ing.riaf.core.exception.IntegrationException;
import nl.ing.serviceclient.lecca.dto.PartyReference;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListRequest;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListResponse;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Lazy
public class ListActivity extends AbstractActivity<List<CreditCard>> {

	private static final String SERVICE_NAME_LECCA = "lecca";

	@Resource
	private CreditcardServiceClientToCreditcardModelConverter creditcardServiceClientToCreditcardModelConverter;

	@Resource
	private RetrieveCreditCardListService retrieveCreditCardListService;

	/**
	 * call the LECCA - list of cards service client
	 * 
	 * @return creditCardList
	 */
	@Override
	protected ActivityOutput<List<CreditCard>> execution(final Object... activityInput) {

		String partyId = activityInput[0].toString();
		
		PartyReferenceDTO partyReference = new PartyReferenceDTO();
		partyReference.setId(partyId);
		partyReference.setType("01");

		final RetrieveCreditCardListRequest request = fillRetrieveCreditCardListRequestData(partyReference);
		final RetrieveCreditCardListResponse response;

		try {
			response = this.retrieveCreditCardListService.retrieveCreditCardListResponse(request);
		} catch (final IntegrationException e) {
			throw new ActivityException(SERVICE_NAME_LECCA, "could not fetch list of cards, exception occurred ", e);
		}

		ActivityOutput.Result result = ActivityOutput.Result.SUCCESS;
		List<CreditCard> creditCardList = new ArrayList<>();
		for (nl.ing.serviceclient.lecca.dto.CreditCard creditcardSC : response.getCreditCardAccountDetails()) {
			CreditCard creditCard = creditcardServiceClientToCreditcardModelConverter.convert(creditcardSC);
			creditCardList.add(creditCard);
		}

		return new ActivityOutput<>(result, creditCardList);
	}

	/**
	 * Fill LECCA party reference with id and type
	 * 
	 * @param partyReference
	 * @return retrieveCreditCardListRequest
	 */
	public RetrieveCreditCardListRequest fillRetrieveCreditCardListRequestData(final PartyReferenceDTO partyReference) {

		final RetrieveCreditCardListRequest retrieveCreditCardListRequest = new RetrieveCreditCardListRequest();
		PartyReference partyReferenceRequestObj = new PartyReference();

		partyReferenceRequestObj.setId(partyReference.getId());
		partyReferenceRequestObj.setType(partyReference.getType());

		retrieveCreditCardListRequest.setParty(partyReferenceRequestObj);

		return retrieveCreditCardListRequest;
	}
}
