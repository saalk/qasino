package applyextra.response;

import com.ing.api.toolkit.encryption.EncryptionUtil;
import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import applyextra.model.ApplyExtraCardDTO;
import applyextra.commons.response.CreditcardFrontendResponse;


@Getter
@Setter
@Slf4j
public class ApplyExtraCardSubmitResponse implements CreditcardFrontendResponse<ApplyExtraCardDTO> {

    private String cramId;
    private String encryptedCramId;
    private String errorCode;

    @Override
    public ApplyExtraCardSubmitResponse extract(ApplyExtraCardDTO dto) {
        if(dto.getRulesCode() == null ||dto.getRulesCode()==0) {
            ChannelContext channelContext = dto.getChannelContext();
            switch (channelContext.getContextSession().getChannelOfOrigin()) {
                case ASSISTED_CALL:
                case ASSISTED_BRANCHES:
                    this.setEncryptedCramId(encryptCramId(dto)); break;
            }
            this.setCramId(dto.getCramDTO().getCramId());
        } else {
            this.setErrorCode(dto.getRulesCode().toString());
        }
        return this;
    }

    // TODO: change to byte[] rather than String. Cannot be processed by Other party at the moment, check with them when.
    private String encryptCramId(final ApplyExtraCardDTO dto) {
        final String sharedSecret = dto.getChannelContext().getContextSession().getSessionSharedSecret();
        return EncryptionUtil.encryptAndEncodeWithPrependedIV(dto.getCramDTO().getCramId(), sharedSecret, true);
    }
}
