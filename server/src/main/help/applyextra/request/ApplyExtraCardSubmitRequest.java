package applyextra.request;

import applyextra.ChannelContext;
import applyextra.commons.request.BaseCreditcardFrontendRequest;
import applyextra.model.ApplyExtraCardDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class ApplyExtraCardSubmitRequest<T extends ApplyExtraCardDTO> extends BaseCreditcardFrontendRequest<T> {

    @Setter(AccessLevel.NONE)
    private String requestId;

    public ApplyExtraCardSubmitRequest(final ChannelContext channelContext,
                                       final String requestId) {
        super(channelContext);
        this.requestId = requestId;
    }

    @Override
    public void inject(T dto) {
        dto.setChannelContext(this.getChannelContext());
        dto.setRequestId(this.requestId);
    }

}
