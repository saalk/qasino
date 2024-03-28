/*
package cloud.qasino.card.move;

import cloud.qasino.card.core.context.ChannelContext;
import cloud.qasino.card.statemachine.GameState;
import cloud.qasino.card.dto.move.AbstractFlowDTO;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.game.QasinoFrontendRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQasinoFlowDTO extends AbstractFlowDTO {

    @Getter
    private Game game;
    private List<QasinoFrontendRequest> frontendRequests = new ArrayList<>();
    @Getter
    private ChannelContext channelContext;

    public void addFrontendRequestInput(QasinoFrontendRequest frontendRequest) {
        this.frontendRequests.add(frontendRequest);
    }

    public void injectFrontendRequestInput() {
        for (QasinoFrontendRequest frontendRequest : this.frontendRequests) {
            frontendRequest.inject(this);
        }
    }

    public AbstractQasinoFlowDTO addQasinoGame(Game context) {
        this.game = context;
        return this;
    }

    public GameState getCurrentState() {
        if (this.game == null) {
            throw new IllegalStateException("Unable to retrieve state");
        } else {
            return this.game.getState();
        }
    }

    public void updateState(GameState newState) {
        if (this.game == null) {
            throw new IllegalStateException("Unable to update state");
        } else {
            this.game.setPreviousState(this.game.getState());
            this.game.setState(newState);
        }
    }

    public void setGameId(int gameId) {
        // this logic exists because the initiation of the creditcardRequest, which requires the request id, but is most
        // logical obtained from the ccrequest
        super.setGameId(gameId);
        if (game != null) {
            this.game.setGameId(gameId);
        }
    }

    public int getGameId() {
        if (game != null) {
            return this.game.getGameId();
        }
        // this logic exists because the initiation of the creditcardRequest, which requires the request id, but is most
        // logical obtained from the ccrequest
        return super.getGameId();
    }

    public abstract Type getContextType();

    public void setChannelContext(ChannelContext channelContext) {
        this.channelContext = channelContext;

        if (this.game.getPlayers().get(0).getUser().getUserId() == 0) {
            this.setRequestorId(channelContext.getCustomers()
                    .get()
                    .getActiveCustomer()
                    .getCustomerId());
        }

        this.setCustomerId(channelContext.getCustomers()
                .get()
                .getActiveCustomer()
                .getCustomerId());

        if (this.game.getChannelType() == null) {
            this.setChannelType(channelContext);
        }

        if (channelContext.getContextSession().getChannelType().isPresent()) {
            this.game.setChannelSubType(channelContext.getContextSession()
                    .getChannelType().orElse("NA")); // if no channel sub type, not applicable
        }

    }

    @Override
    public void setCustomerId(String id) {
        if (this.game != null) {
            this.game.setCustomerId(id);
        }
        this.customerId = id;
    }

    @Override
    public String getCustomerId() {
        if (this.customerId != null) {
            return this.customerId;
        } else {
            return this.game.getCustomerId();
        }
    }

    @Override
    public void setEmployeeId(String id) {
        if (this.game != null) {
            this.game.setRequestorId(id);
        }
        this.employeeId = id;
    }

    public void setRequestorId(String id) {
        this.game.setRequestorId(id);
    }

    public void setChannelType(ChannelContext channelContext) { // FIXME channel type is different from Channel Of
        // Origin
        this.game.setChannelType(channelContext.getContextSession()
                .getChannelOfOrigin()
                .toString());
    }

}
*/
