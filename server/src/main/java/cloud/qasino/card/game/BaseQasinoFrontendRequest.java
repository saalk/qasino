/*
package cloud.qasino.card.game;

import cloud.qasino.card.core.context.ChannelContext;
import cloud.qasino.card.move.AbstractQasinoFlowDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BaseQasinoFrontendRequest<T extends AbstractQasinoFlowDTO> implements QasinoFrontendRequest<T> {

    @Getter
    @Setter
    private ChannelContext channelContext;

    public BaseQasinoFrontendRequest() {
    }

    public BaseQasinoFrontendRequest(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public void inject(T dto) {
        dto.setChannelContext(channelContext);
    }
}
*/
