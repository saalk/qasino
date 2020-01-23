package applyextra.request;

import applyextra.ChannelContext;
import applyextra.commons.request.BaseCreditcardFrontendRequest;
import applyextra.model.ApplyExtraCardDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ApplyExtraCardCheckVerifyRequest<T extends ApplyExtraCardDTO> extends BaseCreditcardFrontendRequest<T> {

    @Setter(AccessLevel.NONE)
    private String beneficiaryId;
    @Setter(AccessLevel.NONE)
    private String requestId;

    public ApplyExtraCardCheckVerifyRequest(final ChannelContext channelContext,
                                            final String requestId,
                                            final String beneficiaryId) {
        super(channelContext);
        this.requestId = requestId;
        this.beneficiaryId = beneficiaryId;
    }

    @Override
    public void inject(T dto) {
        dto.setChannelContext(this.getChannelContext());
        dto.setRequestId(this.requestId);
        if (beneficiaryId != null) {
            dto.setBeneficiaryId(this.beneficiaryId);
        }
    }
}
