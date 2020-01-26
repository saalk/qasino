package applyextra.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import applyextra.model.ApplyExtraCardDTO;
import applyextra.commons.response.CreditcardFrontendResponse;

import javax.ws.rs.ProcessingException;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Slf4j
public class ApplyExtraCardSelectResponse implements CreditcardFrontendResponse<ApplyExtraCardDTO>{


    private String requestId;
    private String iban;
    private String packageType;
    private String errorCode;

    @Override
    public ApplyExtraCardSelectResponse extract(ApplyExtraCardDTO dto) {
        if (dto.getRulesCode() == null || dto.getRulesCode() == 0) {
            //Check if package reference type is null meaning that its not a valid arrangement for this scenario.
            if(dto.getPackageRefType() == null) {
                throw new ProcessingException("Package Type not present.");
            }
            this.setIban(dto.getIban());
            this.setRequestId(dto.getRequestId());
            this.setPackageType(dto.getPackageRefType().toString());
        } else {
            this.setErrorCode(String.valueOf(dto.getRulesCode()));
        }
        return this;
    }
}
