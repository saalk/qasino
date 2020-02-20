package applyextra.commons.event;

import applyextra.ChannelContext;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.request.CreditcardFrontendRequest;
import applyextra.commons.state.CreditCardsStateMachine.State;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreditcardFlowDTO extends AbstractFlowDTO {

    @Getter
    private CreditCardRequestEntity creditcardRequest;
    private List<CreditcardFrontendRequest> frontendRequests = new ArrayList<>();
    @Getter
    private ChannelContext channelContext;

    public void addFrontendRequestInput(CreditcardFrontendRequest frontendRequest) {
        this.frontendRequests.add(frontendRequest);
    }

    public void injectFrontendRequestInput() {
        for (CreditcardFrontendRequest frontendRequest : this.frontendRequests) {
            frontendRequest.inject(this);
        }
    }

    public AbstractCreditcardFlowDTO addCreditcardRequest(CreditCardRequestEntity context) {
        this.creditcardRequest = context;
        return this;
    }

    public State getCurrentState() {
        if (this.creditcardRequest == null) {
            throw new IllegalStateException("Unable to retrieve state");
        } else {
            return this.creditcardRequest.getCurrentState();
        }
    }

    public void updateState(State newState) {
        if (this.creditcardRequest == null) {
            throw new IllegalStateException("Unable to update state");
        } else {
            this.creditcardRequest.setPreviousState(this.creditcardRequest.getCurrentState());
            this.creditcardRequest.setCurrentState(newState);
        }
    }

    public void setRequestId(String requestId) {
        // this logic exists because the initiation of the creditcardRequest, which requires the request id, but is most
        // logical obtained from the ccrequest
        super.setRequestId(requestId);
        if (creditcardRequest != null) {
            this.creditcardRequest.setId(requestId);
        }
    }

    public String getRequestId() {
        if (creditcardRequest != null) {
            return this.creditcardRequest.getId();
        }
        // this logic exists because the initiation of the creditcardRequest, which requires the request id, but is most
        // logical obtained from the ccrequest
        return super.getRequestId();
    }

    public abstract RequestType getContextType();

    public void setChannelContext(ChannelContext channelContext) {
        this.channelContext = channelContext;

        if (this.creditcardRequest.getRequestorId() == null) {
            this.setRequestorId(channelContext.getCustomers()
                    .get()
                    .getActiveCustomer()
                    .getCustomerId());
        }

        this.setCustomerId(channelContext.getCustomers()
                .get()
                .getActiveCustomer()
                .getCustomerId());

        if (this.creditcardRequest.getChannelType() == null) {
            this.setChannelType(channelContext);
        }

        if (channelContext.getContextSession().getChannelType().isPresent()) {
            this.creditcardRequest.setChannelSubType(channelContext.getContextSession()
                    .getChannelType().orElse("NA")); // if no channel sub type, not applicable
        }

    }

    @Override
    public void setCustomerId(String id) {
        if (this.creditcardRequest != null) {
            this.creditcardRequest.setCustomerId(id);
        }
        this.customerId = id;
    }

    @Override
    public String getCustomerId() {
        if (this.customerId != null) {
            return this.customerId;
        } else {
            return this.creditcardRequest.getCustomerId();
        }
    }

    @Override
    public void setEmployeeId(String id) {
        if (this.creditcardRequest != null) {
            this.creditcardRequest.setRequestorId(id);
        }
        this.employeeId = id;
    }

    public void setRequestorId(String id) {
        this.creditcardRequest.setRequestorId(id);
    }

    public void setChannelType(ChannelContext channelContext) { // FIXME channel type is different from Channel Of
        // Origin
        this.creditcardRequest.setChannelType(channelContext.getContextSession()
                .getChannelOfOrigin()
                .toString());
    }

}
