package applyextra.request;

import applyextra.ChannelContext;
import applyextra.commons.request.BaseCreditcardFrontendRequest;
import applyextra.model.ApplyExtraCardDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class ApplyExtraCardSelectCardRequest<T extends ApplyExtraCardDTO> extends BaseCreditcardFrontendRequest<T> {

    @Setter(AccessLevel.NONE)
    private String accountNumber;

    @Setter(AccessLevel.NONE)
    private String creditCardId;

    public ApplyExtraCardSelectCardRequest(ChannelContext channelContext, String accountNumber, String creditCardId) {
        super(channelContext);
        this.accountNumber = accountNumber;
        this.creditCardId = creditCardId;
    }

    @Override
    public void inject(T dto) {
        dto.setSelectedCardId(creditCardId);
        dto.setChannelContext(this.getChannelContext());

        if (this.accountNumber != null) {
            dto.setSiaAccountNumber(this.accountNumber);
        }
    }
}
