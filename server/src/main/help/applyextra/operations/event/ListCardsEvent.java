package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.filter.AbstractFilter;
import applyextra.commons.filter.PortfolioCodeFilter;
import applyextra.commons.filter.SelectedCardFilter;
import applyextra.commons.filter.StatusFilter;
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
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class ListCardsEvent extends AbstractEvent {
    private static final String SERVICE_NAME_LECCA = "lecca";
    private static final int START_LAST_CARDNUMBER_SECTION = 12;

    @Resource
    private RetrieveCreditCardListService retrieveCreditCardListService;
    @Resource
    private CreditcardServiceClientToCreditcardModelConverter creditcardServiceClientToCreditcardModelConverter;

    private Map<String, AbstractFilter> filterRegistry = new HashMap<>();
    public ListCardsEvent() {
        filterRegistry.put("portfolio", new PortfolioCodeFilter());
        filterRegistry.put("state", new StatusFilter());
    }

    /**
     * call the LECCA - list of cards service client and returns one creditCard
     *
     * @return creditCardList
     */
    @Override
    public EventOutput execution(final Object... eventInput) {
        ListCardsEventDTO flowDTO = (ListCardsEventDTO) eventInput[0];
        String partyId = flowDTO.getPartyId();
        String creditCardId = flowDTO.getCreditCardId();

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

        List<AbstractFilter> creditCardFilter = makeFilterList(flowDTO.getCreditCardFilter());
        List<CreditCard> creditCardList = new ArrayList<>();

        final EventOutput result;
        if (creditCardId == null) {
            filterCards(response.getCreditCardList(), creditCardList, creditCardFilter);
            result = new EventOutput(EventOutput.Result.SUCCESS);
        } else {
			creditCardFilter.add(new SelectedCardFilter(creditCardId));
			filterCards(response.getCreditCardList(), creditCardList, creditCardFilter);
			if(creditCardList.size() == 1){
				result = new EventOutput(EventOutput.Result.SUCCESS);
			}
			else{
				result = new EventOutput(EventOutput.Result.FAILURE);
			}
        }
        flowDTO.setListCardResult(result.isSuccess());
        linkExtraCardsToMainCard(creditCardList);
        flowDTO.getCreditCards().addAll(creditCardList);
        return result;
    }

    private void linkExtraCardsToMainCard(final List<CreditCard> creditCardList) {
        final Map<String, CreditCard> mainCardsPerArrangement = new HashMap<>();
        for (CreditCard creditCard : creditCardList) {
            if ("Primary".equals(creditCard.getType())) {
                mainCardsPerArrangement.put(creditCard.getArrangementNumber(), creditCard);
            }
        }
        for (CreditCard creditCard : creditCardList) {
            if ("Secondary".equals(creditCard.getType())) {
                final CreditCard mainCard = mainCardsPerArrangement.get(creditCard.getArrangementNumber());
                if (mainCard != null) {
                    creditCard.setLinkedMainCardId(mainCard.getId());
                }
            }
        }
    }

    private List<AbstractFilter> makeFilterList(final MultivaluedMap<String, String> creditCardFilter) {
        List<AbstractFilter> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : creditCardFilter.entrySet()) {
            AbstractFilter filter = filterRegistry.get(entry.getKey());
            if (filter == null) {
                log.warn("Unknown parameter: " + entry.getKey());
            } else {
                filter = filter.makeNew();
                filter.setFilterValue(entry.getValue());
                result.add(filter);
            }
        }
        return result;
    }

    private void filterCards(final List<nl.ing.serviceclient.lecca.dto.CreditCard> source, final List<CreditCard> dest, final List<AbstractFilter> filters) {
        for (nl.ing.serviceclient.lecca.dto.CreditCard creditcardSC : source) {
            boolean mustInclude = true;
            for (AbstractFilter filter : filters) {
                mustInclude = filter.matches(creditcardSC);
                if (!mustInclude) {
                    break;
                }
            }
            if (mustInclude) {
                CreditCard creditCard = creditcardServiceClientToCreditcardModelConverter.convert(creditcardSC);
                dest.add(creditCard);
            }
        }
    }

    /**
     * Fill LECCA party reference with id and type
     *
     * @return retrieveCreditCardListRequest
     */
    private RetrieveCreditCardListRequest fillRetrieveCreditCardListRequestData(final PartyReferenceDTO partyReference) {

        final RetrieveCreditCardListRequest retrieveCreditCardListRequest = new RetrieveCreditCardListRequest();
        PartyReference partyReferenceRequestObj = new PartyReference();

        partyReferenceRequestObj.setId(partyReference.getId());
        partyReferenceRequestObj.setType(partyReference.getType());

        retrieveCreditCardListRequest.setParty(partyReferenceRequestObj);

        return retrieveCreditCardListRequest;
    }

    public interface ListCardsEventDTO {
        String getPartyId();
        String getCreditCardId();
        MultivaluedMap<String, String> getCreditCardFilter();
        List<CreditCard> getCreditCards();
        void setListCardResult(boolean result);
    }
}