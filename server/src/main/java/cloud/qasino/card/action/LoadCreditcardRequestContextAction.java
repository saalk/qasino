package cloud.qasino.card.action;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.event.AbstractQasinoFlowDTO;
import cloud.qasino.card.orchestration.Action;
import cloud.qasino.card.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

import static cloud.qasino.card.orchestration.EventEnum.ENTER_STATE;

@Component
@Slf4j
public class LoadCreditcardRequestContextAction implements Action<AbstractQasinoFlowDTO, Boolean> {

    @Resource
    private GameRepository gameRepository;
/*
    @Resource
    private LoadPartyFinancialDataAction financialDataAction;
*/

    @Override
    public Boolean perform(final AbstractQasinoFlowDTO dto) {
        //request does not have to be loaded again if next state is processed in succession
        if (ENTER_STATE != dto.getCurrentEvent()) {

            Game game;
            Optional<Game> optionalGame;

            if (dto.getGameId() == 0) {
                game = gameRepository.save(dto.getGame());
            } else {
                optionalGame = gameRepository.findById(dto.getGameId());
                game = optionalGame.get();
            }

            dto.addQasinoGame(game);

/*            //If the dto contains financialacceptance dto, also load that data
            if (dto instanceof FinancialAcceptanceDTO) {
                financialDataAction.perform((FinancialAcceptanceDTO) dto);
                if (game.getAccount() != null && game.getAccount().getAccountStatus() != null) {
                    ((FinancialAcceptanceDTO) dto).setPortfolioCode(game.getAccount().getAccountStatus().getPortfolioCode());
                }
            }*/
            dto.injectFrontendRequestInput();
        }
        return true;
    }

}
