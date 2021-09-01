package cloud.qasino.quiz.statemachine.configuration;

import cloud.qasino.quiz.controller.AsyncEventHandlerService;
import cloud.qasino.quiz.event.interfaces.AbstractFlowDTO;
import cloud.qasino.quiz.event.interfaces.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class QasinoAsyncConfiguration implements AsyncConfigurer {

    public static final String ASYNC_EXECUTOR_THREAD_NAME = "EventHandler-Executor";

    @Value("${param.async.corePoolSize:10}")
    private static String corePoolSize; // dev = default

    @Value("${param.async.maxPoolSize:20}")
    private static String maxPoolSize; // dev = default

    @Value("${param.async.queueCapacity:500}")
    private static String queueCapacity; // dev = default

    @Bean(name = ASYNC_EXECUTOR_THREAD_NAME) // name = Thread.getName()
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // todo check why @value does not work
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(500);
        return threadPoolTaskExecutor;
    }

    /**
     * Bean that handles the Exception from the threads which are executed during the ASync.
     * All Exceptions should already be handled by the move handler, but this acts as a final backup.
     *
     * @return Anonymous exception which logs the error to the application.log
     */
    @Bean
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> log.error("Exception during executing of an asynchronous move", throwable);
    }

    /**
     * Interface needs to be implemented where the EventHandler is configured in the API.
     * This Configuration should be used together with the {@link AsyncEventHandlerService}.
     *
     * @Async annotated method in the {@link AsyncEventHandlerService} uses this interface to get the bean and be able to execute the target move.
     */
    public interface ASyncEventHandler {
        <T extends AbstractFlowDTO> T handleEvent(Event event, T dto);
    }
}
