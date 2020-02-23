package applyextra.controller;

import applyextra.ChannelContext;
import applyextra.commons.controllers.AsyncEventHandlerService;
import applyextra.commons.orchestration.EventEnum;
import applyextra.commons.request.BaseCreditcardFrontendRequest;
import applyextra.model.ApplyExtraCardDTO;
import applyextra.operations.dto.CramStatus;
import applyextra.operations.model.CramCustomerRequest;
import applyextra.request.ApplyExtraCardCheckVerifyRequest;
import applyextra.request.ApplyExtraCardSelectCardRequest;
import applyextra.request.ApplyExtraCardSubmitRequest;
import applyextra.response.ApplyExtraCardCheckAndVerifyResponse;
import applyextra.response.ApplyExtraCardSelectResponse;
import applyextra.response.ApplyExtraCardSubmitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalTime;

import static applyextra.commons.orchestration.EventEnum.*;

@Slf4j
@Component
public class ApplyExtraCardController {

    @Resource
    private ApplyExtraCardStateMachine applyExtraCardStateMachine;
    @Resource
    private AsyncEventHandlerService fulfillmentHandler;


    public ApplyExtraCardSelectResponse initialize(final ChannelContext channelContext,
                                                   final String customerId,
                                                   final String accountNumber,
                                                   final String creditCardId) {
        log.info("########## Start of initialize: " + LocalTime.now());
        log.info("########## Start of initialize: " + LocalTime.now());

        // Add request from Resource, and extract Response from FlowDTO.
        ApplyExtraCardSelectCardRequest request = new ApplyExtraCardSelectCardRequest(channelContext, accountNumber, creditCardId);
        ApplyExtraCardDTO flowDTO = new ApplyExtraCardDTO();
        flowDTO.setCustomerId(customerId);
        flowDTO.addFrontendRequestInput(request);

        applyExtraCardStateMachine.handleEvent(LIST, flowDTO);
        log.info("########## Start of initialize: " + LocalTime.now());

        return new ApplyExtraCardSelectResponse().extract(flowDTO);
    }

    public ApplyExtraCardCheckAndVerifyResponse check(final ChannelContext channelContext,
                                                      final String requestId,
                                                      final String beneficiaryId) {
        return checkAndVerify(channelContext, requestId, beneficiaryId, CHECK);
    }

    public ApplyExtraCardCheckAndVerifyResponse verify(final ChannelContext channelContext,
                                                       final String requestId,
                                                       final String beneficiaryId) {
        return checkAndVerify(channelContext, requestId, beneficiaryId, VERIFY);
    }

    private ApplyExtraCardCheckAndVerifyResponse checkAndVerify(final ChannelContext channelContext,
                                                                final String requestId,
                                                                final String beneficiaryId,
                                                                final EventEnum targetEvent) {
        ApplyExtraCardCheckVerifyRequest request = new ApplyExtraCardCheckVerifyRequest(channelContext, requestId, beneficiaryId);
        ApplyExtraCardDTO flowDTO = new ApplyExtraCardDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.addFrontendRequestInput(request);

        applyExtraCardStateMachine.handleEvent(targetEvent, flowDTO);
        return new ApplyExtraCardCheckAndVerifyResponse().extract(flowDTO);
    }


    public ApplyExtraCardSubmitResponse submit(final ChannelContext channelContext, final String requestId) {
        ApplyExtraCardSubmitRequest request = new ApplyExtraCardSubmitRequest(channelContext, requestId);
        ApplyExtraCardDTO flowDTO = new ApplyExtraCardDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.addFrontendRequestInput(request);

        applyExtraCardStateMachine.handleEvent(SUBMIT, flowDTO);
        return new ApplyExtraCardSubmitResponse().extract(flowDTO);
    }

    public void authorize(final CramCustomerRequest customerRequest) {
        String requestId = customerRequest.getCustomerRequest()
                .getExternalReference()
                .getId();
        ApplyExtraCardDTO flowDTO = new ApplyExtraCardDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.getCramDTO().setStatus(CramStatus.fromString(customerRequest.getCustomerRequest().getStatus().toString()));

        switch (customerRequest.getCustomerRequest().getStatus()) {
            case Cancelled:
                applyExtraCardStateMachine.handleEvent(CANCEL, flowDTO);
                break;
            case Expired:
                applyExtraCardStateMachine.handleEvent(REJECT, flowDTO);
                break;
            default:
                applyExtraCardStateMachine.handleEvent(AUTHORIZE, flowDTO);
                fulfillmentHandler.scheduleEvent(applyExtraCardStateMachine, VERIFY, flowDTO);
                break;
        }
    }

    public void reset(final ChannelContext channelContext,
                      final String requestId) {
        ApplyExtraCardDTO flowDTO = new ApplyExtraCardDTO();
        flowDTO.setRequestId(requestId);
        flowDTO.addFrontendRequestInput(new BaseCreditcardFrontendRequest(channelContext));

        applyExtraCardStateMachine.handleEvent(RESET, flowDTO);
    }


}
