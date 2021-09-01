package cloud.qasino.quiz.controller;/*
package cloud.qasino.quiz.controller;

import cloud.qasino.quiz.statemachine.QasinoStateMachine;
import cloud.qasino.quiz.core.context.ChannelContext;
import cloud.qasino.quiz.dto.QasinoFlowDTO;
import cloud.qasino.quiz.event.EventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalTime;

@Slf4j
@Component
public class QasinoController {

    @Resource
    private QasinoStateMachine qasinoStateMachine;
    @Resource
    private AsyncEventHandlerService fulfillmentHandler;


    public QasinoSelectResponse initialize(final ChannelContext channelContext,
                                                   final String userId,
                                                   final String accountNumber,
                                                   final String creditQuizId) {
        log.info("########## Start of initialize: " + LocalTime.now());
        log.info("########## Start of initialize: " + LocalTime.now());

        // Add request from Resource, and extract Response from FlowDTO.
        QasinoSelectQuizRequest request = new QasinoSelectQuizRequest(channelContext, accountNumber, creditQuizId);
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setUserId(userId);
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

    public QasinoSubmitResponse submit(final ChannelContext channelContext, final String requestId) {
        QasinoSubmitRequest request = new QasinoSubmitRequest(channelContext, requestId);
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.addFrontendRequestInput(request);

        qasinoStateMachine.handleEvent(SUBMIT, flowDTO);
        return new QasinoSubmitResponse().extract(flowDTO);
    }

    public void authorize(final CramUserRequest userRequest) {
        String requestId = userRequest.getUserRequest()
                .getExternalReference()
                .getId();
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.getCramDTO().setStatus(CramStatus.fromString(userRequest.getUserRequest().getStatus().toString()));

        switch (userRequest.getUserRequest().getStatus()) {
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
        QasinoFlowDTO flowDTO = new QasinoFlowDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.addFrontendRequestInput(new BaseCreditquizFrontendRequest(channelContext));

        qasinoStateMachine.handleEvent(RESET, flowDTO);
    }


}
*/
