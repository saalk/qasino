package applyextra.commons.resource;

import com.ing.api.toolkit.trust.context.ChannelContext;
import applyextra.commons.audit.AuditDelegate;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.util.MiscUtils;

import javax.annotation.Resource;
import javax.validation.Validator;

public abstract class AbstractResource {

	@Resource
	private CreditcardRequestService ccRequestService;
	@Resource
	protected AuditDelegate auditDelegate;
    @Resource
    private Validator validator;

	public final AuditDelegate getAuditDelegate() {
		return auditDelegate;
	}

	/**
	 * Checks if the request is initiated by the current user
	 *
	 * @param channelContext User context as received from RIAF
	 * @param requestId requestId from the GET/POST http request
	 */
	protected final boolean verifyCreditcardRequest(final ChannelContext channelContext, final String requestId) {
		if (!ccRequestService.verifyRequest(requestId, channelContext)) {
			auditDelegate.fireSecurityEvent("The user does not match with the request.");
			return false;
		}
		return true;
	}

    /**
     * Validates the input
     * @param field - input field
     */
    protected final void validate(final Object field) {
        MiscUtils.validate(field, validator);
    }

}
