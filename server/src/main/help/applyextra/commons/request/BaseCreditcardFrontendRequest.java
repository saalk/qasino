package applyextra.commons.request;

import applyextra.ChannelContext;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BaseCreditcardFrontendRequest<T extends AbstractCreditcardFlowDTO> implements CreditcardFrontendRequest<T> {

    @Getter
    @Setter
    private ChannelContext channelContext;

    public BaseCreditcardFrontendRequest() {
    }

    public BaseCreditcardFrontendRequest(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public void inject(T dto) {
        dto.setChannelContext(channelContext);
    }
}
