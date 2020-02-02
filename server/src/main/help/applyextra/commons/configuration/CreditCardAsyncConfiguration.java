package applyextra.commons.configuration;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.controllers.AsyncEventHandlerService;
import applyextra.commons.event.AbstractFlowDTO;
import applyextra.commons.orchestration.Event;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class CreditCardAsyncConfiguration implements AsyncConfigurer {

    public static final String ASYNC_EXECUTOR_THREAD_NAME = "EventHandler-Executor";

    @Resource
    private JNDIUtil jndiUtil;

    @Bean(name = ASYNC_EXECUTOR_THREAD_NAME) // name = Thread.getName()
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(jndiUtil.getJndiValueWithDefault("/param/async/corePoolSize", 10));
        threadPoolTaskExecutor.setMaxPoolSize(jndiUtil.getJndiValueWithDefault("/param/async/maxPoolSize", 20));
        threadPoolTaskExecutor.setQueueCapacity(jndiUtil.getJndiValueWithDefault("/param/async/queueCapacity", 5000));
        return threadPoolTaskExecutor;
    }

    /**
     * Bean that handles the Exception from the threads which are executed during the ASync.
     * All Exceptions should already be handled by the move handler, but this acts as a final backup.
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
