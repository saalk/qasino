package applyextra.operations.activity;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.model.CreditCard;
import applyextra.operations.converters.CreditcardServiceClientToCreditcardModelConverter;
import applyextra.operations.dto.PartyReferenceDTO;
import nl.ing.riaf.core.exception.IntegrationException;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListRequest;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListResponse;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@Lazy
public class CreditcardActivity extends AbstractActivity<CreditCard> {
	private static final String SERVICE_NAME_LECCA = "lecca";

	private static final int START_LAST_CARDNUMBER_SECTION = 12;

	@Resource
	private RetrieveCreditCardListService retrieveCreditCardListService;

	@Resource
	private CreditcardServiceClientToCreditcardModelConverter creditcardServiceClientToCreditcardModelConverter;
	
	@Resource
	private ListActivity listActivity;

	/**
	 * call the LECCA - list of cards service client and returns one creditCard
	 * 
	 * @return creditCardList
	 */
	@Override
	protected ActivityOutput<CreditCard> execution(final Object... activityInput) {

		String partyId = activityInput[0].toString();
		String creditCardId = String.valueOf(activityInput[1]);
		PartyReferenceDTO partyReference = new PartyReferenceDTO();
		partyReference.setId(partyId);
		partyReference.setType("01");

		final RetrieveCreditCardListRequest request = listActivity.fillRetrieveCreditCardListRequestData(partyReference);
		final RetrieveCreditCardListResponse response;

		try {
			response = this.retrieveCreditCardListService.retrieveCreditCardListResponse(request);
		} catch (final IntegrationException e) {
			throw new ActivityException(SERVICE_NAME_LECCA, "could not fetch list of cards, exception occurred ", e);
		}

		for (nl.ing.serviceclient.lecca.dto.CreditCard creditCardSC : response.getCreditCardAccountDetails()) {
			if (creditCardSC.getCreditCardNumber().substring(START_LAST_CARDNUMBER_SECTION).equals(creditCardId)) {
				CreditCard creditCard = creditcardServiceClientToCreditcardModelConverter.convert(creditCardSC);
				creditCard.setCurrentAccount(response.getAccountNumber());
				return new ActivityOutput<>(ActivityOutput.Result.SUCCESS, creditCard);
			}
		}
		return new ActivityOutput<>(ActivityOutput.Result.FAILURE);

	}

}
