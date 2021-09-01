package cloud.qasino.quiz.controller;

import cloud.qasino.quiz.statemachine.configuration.QasinoAsyncConfiguration;
import cloud.qasino.quiz.event.EventEnum;
import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static cloud.qasino.quiz.statemachine.configuration.QasinoAsyncConfiguration.ASYNC_EXECUTOR_THREAD_NAME;


@Lazy
@Slf4j
@Component
public class AsyncEventHandlerService {

    /**
     * Method to schedule the execution of the Events asynchronously.
     * Uses {@link QasinoAsyncConfiguration} to enable the Async call. If not imported, it will execute synchronously.
     *
     * @param ASyncHandler implementation of {@link QasinoAsyncConfiguration.ASyncEventHandler}. Allows inversion of control
     * @param targetEnum   Should be the move you wish to trigger for the next state transition in your api.
     * @param dto          Should be your DTO.
     */
    @Async(ASYNC_EXECUTOR_THREAD_NAME)
    public void scheduleEvent(QasinoAsyncConfiguration.ASyncEventHandler ASyncHandler, EventEnum targetEnum, AbstractFlowDTO dto) {
        log.info("Scheduled Event for gameId: " + dto.getGameId());
        // Exception will be taken care off by the AsyncUncaughtExceptionHandler in the config.
        ASyncHandler.handleEvent(targetEnum, dto);
    }

}
