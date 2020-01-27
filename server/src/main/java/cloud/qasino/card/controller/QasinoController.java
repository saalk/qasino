package cloud.qasino.card.controller;

import cloud.qasino.card.controller.statemachine.EventState;
import cloud.qasino.card.controller.statemachine.EventTrigger;
import cloud.qasino.card.controller.statemachine.QasinoStateMachine;
import cloud.qasino.card.dto.QasinoFlowDTO;
import cloud.qasino.card.dto.event.FlowDTOBuilder;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.error.ErrorResponse;
import cloud.qasino.card.error.HttpError;
import cloud.qasino.card.response.QasinoResponse;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*static {
        // @formatter:off
        // start on player page
        config.configure(EventState.INIT) // no events yes
        .permit(EventTrigger.START, EventState.NEW)
        ;


        config.configure(EventState.NEW) // new/first turn started
        .permit(EventTrigger.START, EventState.NEW)

        .permit(EventTrigger.STOP, EventState.HIGHER)
        .permit(EventTrigger.STOP, EventState.LOWER);

        config.configure(EventState.DEALT)
        .permit(EventTrigger.HIGHER, EventState.HIGHER)
        .permit(EventTrigger.HIGHER, EventState.LOWER)
        .permit(EventTrigger.LOWER, EventState.HIGHER)
        .permit(EventTrigger.LOWER, EventState.LOWER);

        config.configure(EventState.HIGHER)
        .permitReentry(EventTrigger.HIGHER)
        .permitReentry(EventTrigger.LOWER)
        .permitReentry(EventTrigger.STOP)
        .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.HIGHER)
        .permitReentry(EventTrigger.DEAL)
        .permit(EventTrigger.WINNER, EventState.FINISHED)
        .permit(EventTrigger.CRASH, EventState.ERROR);

        config.configure(EventState.LOWER)
        .permit(EventTrigger.DEAL, EventState.PLAYING); // allows continue other players!

        // @formatter:on
        }

                flowDTO.processPathAndQueryParamsAndTrigger(pathAndQueryData, EventTrigger);

        final Integer rulesCode = flowDTO.getRulesCode();
        if (rulesCode != null && rulesCode != 0) {
            responseBuilder.errorCode(rulesCode.toString());
            responseBuilder.reason(QasinoResponse.Reason.FAILURE);
        } else {
            responseBuilder.errorCode(null);
            responseBuilder.errorMessage(null);
            responseBuilder.solution(null);
            responseBuilder.reason(QasinoResponse.Reason.SUCCESS);
        }


        */

@Slf4j
@Component
// @Scope("prototype") why needed
public class QasinoController {

        @Resource
        private QasinoStateMachine qasinoStateMachine;
        @Resource
        private AsyncEventHandlerService fulfillmentHandler;


        public QasinoSelectResponse initialize(final ChannelContext channelContext,
                                                       final String customerId,
                                                       final String accountNumber,
                                                       final String creditCardId) {
            log.info("########## Start of initialize: " + LocalTime.now());
            log.info("########## Start of initialize: " + LocalTime.now());

            // Add request from Resource, and extract Response from FlowDTO.
            QasinoSelectCardRequest request = new QasinoSelectCardRequest(channelContext, accountNumber, creditCardId);
            QasinoDTO flowDTO = new QasinoDTO();
            flowDTO.setCustomerId(customerId);
            flowDTO.addFrontendRequestInput(request);

            qasinoStateMachine.handleEvent(LIST, flowDTO);
            log.info("########## Start of initialize: " + LocalTime.now());

            return new QasinoSelectResponse().extract(flowDTO);
        }

        public QasinoCheckAndVerifyResponse check(final ChannelContext channelContext,
                                                          final String requestId,
                                                          final String beneficiaryId) {
            return checkAndVerify(channelContext, requestId, beneficiaryId, CHECK);
        }

        public QasinoCheckAndVerifyResponse verify(final ChannelContext channelContext,
                                                           final String requestId,
                                                           final String beneficiaryId) {
            return checkAndVerify(channelContext, requestId, beneficiaryId, VERIFY);
        }

        private QasinoCheckAndVerifyResponse checkAndVerify(final ChannelContext channelContext,
                                                                    final String requestId,
                                                                    final String beneficiaryId,
                                                                    final EventEnum targetEvent) {
            QasinoCheckVerifyRequest request = new QasinoCheckVerifyRequest(channelContext, requestId, beneficiaryId);
            QasinoDTO flowDTO = new QasinoDTO();
            flowDTO.setRequestId(requestId);
            flowDTO.addFrontendRequestInput(request);

            qasinoStateMachine.handleEvent(targetEvent, flowDTO);
            return new QasinoCheckAndVerifyResponse().extract(flowDTO);
        }


        public QasinoSubmitResponse submit(final ChannelContext channelContext, final String requestId) {
            QasinoSubmitRequest request = new QasinoSubmitRequest(channelContext, requestId);
            QasinoDTO flowDTO = new QasinoDTO();
            flowDTO.setRequestId(requestId);
            flowDTO.addFrontendRequestInput(request);

            qasinoStateMachine.handleEvent(SUBMIT, flowDTO);
            return new QasinoSubmitResponse().extract(flowDTO);
        }

        public void authorize(final CramCustomerRequest customerRequest) {
            String requestId = customerRequest.getCustomerRequest()
                    .getExternalReference()
                    .getId();
            QasinoDTO flowDTO = new QasinoDTO();
            flowDTO.setRequestId(requestId);
            flowDTO.getCramDTO().setStatus(CramStatus.fromString(customerRequest.getCustomerRequest().getStatus().toString()));

            switch (customerRequest.getCustomerRequest().getStatus()) {
                case Cancelled:
                    qasinoStateMachine.handleEvent(CANCEL, flowDTO);
                    break;
                case Expired:
                    qasinoStateMachine.handleEvent(REJECT, flowDTO);
                    break;
                default:
                    qasinoStateMachine.handleEvent(AUTHORIZE, flowDTO);
                    fulfillmentHandler.scheduleEvent(qasinoStateMachine, VERIFY, flowDTO);
                    break;
            }
        }

        public void reset(final ChannelContext channelContext,
                          final String requestId) {
            QasinoDTO flowDTO = new QasinoDTO();
            flowDTO.setRequestId(requestId);
            flowDTO.addFrontendRequestInput(new BaseCreditcardFrontendRequest(channelContext));

            qasinoStateMachine.handleEvent(RESET, flowDTO);
        }


}

