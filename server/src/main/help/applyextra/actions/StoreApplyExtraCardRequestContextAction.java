package applyextra.actions;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.configuration.Constants;
import applyextra.api.listaccounts.creditcards.value.*;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.AccountStatusEntity;
import applyextra.commons.model.database.entity.CreditCardAccountEntity;
import applyextra.commons.model.database.entity.CreditCardEntity;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.financialdata.CardType;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.SecurityEncryptionUtil;
import applyextra.operations.event.PersistProcessSpecificValue;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class to store the necessary details.
 */
@Slf4j
@Lazy
@Component
public class  StoreApplyExtraCardRequestContextAction implements Action<StoreApplyExtraCardRequestContextAction.StoreRequestContextDTO, EventOutput.Result> {

    @Resource
    private SecurityEncryptionUtil securityEncryptionUtil;
    @Resource
    private PersistSecondaryCardAction persistSecondaryCardAction;

    @Override
    public EventOutput.Result perform(StoreRequestContextDTO flowDTO) {
        log.info("########## Start of move Perform StoreApplyExtraCardRequestContextAction: " + LocalTime.now());

        final CreditCardRequestEntity request = flowDTO.getCreditcardRequest();
        final CreditCardAccount account = flowDTO.getCreditCardAccount();
        final String secondaryAccountNumber = flowDTO.getSecondaryAccountNumber();

        if (account == null || request == null) {
            throw new ActivityException(Constants.APPLICATION_ID, "No account provided to store in requestContext");
        }

        mapAccountToAccountEntity(
                flowDTO.getChannelContext(),
                request.getAccount(),
                account);

        mapAccountToAccountStatusEntity(
                request.getAccount().getAccountStatus(),
                account);

        //Persists the secondary account number
        if(StringUtils.isNotEmpty(secondaryAccountNumber)) {
            flowDTO.setProcessSpecificValue(Constants.SECONDARY_ACCOUNT_NUMBER, flowDTO.getSecondaryAccountNumber());
            persistSecondaryCardAction.perform(flowDTO);
            log.info("The secondaryAccountNumber has been persisted in the process specific data table");
        }

        if (flowDTO.getSelectedCreditCardId() != null) {
            mapCreditCardToCreditCardEntity(
                    request.getAccount().getCreditCard(),
                    account,
                    flowDTO.getSelectedCreditCardId()
            );
        }
        log.info("########## End of move Perform StoreApplyExtraCardRequestContextAction: " + LocalTime.now());
        return EventOutput.Result.SUCCESS;
    }

    // Maps the values form the Listaccounts to the Account in our request table.
    private void mapAccountToAccountEntity (ChannelContext channelContext,
                                            CreditCardAccountEntity targetEntity,
                                            CreditCardAccount fromEntity) {
        targetEntity.setAccountNumber(securityEncryptionUtil.decrypt(fromEntity.getEncryptedAccountNumber(), channelContext));
        targetEntity.setIban(fromEntity.getIban());
    }

    // Maps the values form the Listaccounts to the AccountStatus in our request table.
    private void mapAccountToAccountStatusEntity (AccountStatusEntity targetEntity,
                                                  CreditCardAccount fromEntity) {

        targetEntity.setPortfolioCode(determineCardPortfolioCode(fromEntity.getCreditCardPortfolioCode()));
        targetEntity.setCardType(determineCardType(fromEntity.getCreditCardProductName()));
        targetEntity.setCreditLimit(fromEntity.getAssignedCreditLimit().intValue());
    }



    // ListAccounts Enum to RequestDatabaseEnum
    private PortfolioCode determineCardPortfolioCode (CreditCardPortfolioCode input) {
        PortfolioCode result = null;
        if (input != null) {
            switch (input){
                case CHARGE: result = PortfolioCode.CHARGE; break;
                case REVOLVING: result = PortfolioCode.REVOLVING; break;
            }
        }
        return result;
    }

    // ListAccounts Enum to RequestDatabaseEnum
    private CardType determineCardType (CreditCardProductName input) {
        CardType result = null;
        if (input != null ) {
            switch (input) {
                case CREDITCARD: result = CardType.Creditcard; break;
                case PLATINUMCARD: result = CardType.Platinumcard; break;
            }
        }
        return result;
    }


    // MMaps the values form the Listaccounts to the Creditcard in our request table.
    private void mapCreditCardToCreditCardEntity(CreditCardEntity targetEntity,
                                                 CreditCardAccount account,
                                                 String cardId) {
        final List<CreditCard> allAccounts = new ArrayList<>();
        if (account.getCreditCardList() != null) {
            allAccounts.addAll(account.getCreditCardList());
        }
        final Optional<CreditCard> selectedCard = findCard(allAccounts, account, cardId);

        if (selectedCard.isPresent()) {
            storeCreditCard(targetEntity, selectedCard.get());
        } else {
            log.warn("Could not find the creditcard to store in the requestContext for cardid:" + cardId);
        }
    }

    private Optional<CreditCard> findCard(List<CreditCard> allAccounts,
                                          CreditCardAccount account,
                                          String cardId) {

        final List<CreditCardMemberAccount> memberAccounts = account.getCreditCardMemberAccountList();
        if (account.getCreditCardMemberAccountList() != null) {
            for (CreditCardMemberAccount member : memberAccounts) {
                allAccounts.addAll(member.getCreditCardList());
            }
        }
        return allAccounts.stream()
                .filter(creditCard -> creditCard.getCreditCardNumber()
                        .substring(creditCard.getCreditCardNumber().length() - 4).equals(cardId))
                .findFirst();
    }

    private void storeCreditCard(CreditCardEntity targetEntity, CreditCard selectedCard) {
        targetEntity.setCardStatus(selectedCard.getCreditCardStatus().getCreditCardStatus());
        targetEntity.setCardUser(selectedCard.getEmbossingName1());

        // Cannot be outside of method due to race conditions problems with simpleDateFormat.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            if (null != selectedCard.getExpirationDate()) {
                targetEntity.setExpirationDate(sdf.parse(selectedCard.getExpirationDate()));
            }
            if (null != selectedCard.getActivationDate()) {
                targetEntity.setStartDate(sdf.parse(selectedCard.getActivationDate()));
            }
        } catch (ParseException e) {
            log.warn("Could not parse the Experation Date: " + selectedCard.getExpirationDate() + ", or the Activation date: " + selectedCard.getActivationDate());
        }
    }


    public interface StoreRequestContextDTO extends PersistProcessSpecificValue.ProcessSpecificDataActionDTO {
        CreditCardRequestEntity getCreditcardRequest();
        CreditCardAccount getCreditCardAccount();
        ChannelContext getChannelContext();
        String getSecondaryAccountNumber();

        // Overwrite to set a creditcard from ListAccounts in the requestTable. Should return last 4 digits
        default String getSelectedCreditCardId() { return null; }
    }
}

