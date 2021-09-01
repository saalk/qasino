package cloud.qasino.quiz.scheduling;

import cloud.qasino.quiz.scheduling.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Scheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static class ScheduledTask {
        private cloud.qasino.quiz.scheduling.Task task;
        private String lastMessage;
        private Date scheduledNextExecution;
        private int nrofInvocations;
        private int nrofFailedInvocations;

        public ScheduledTask(cloud.qasino.quiz.scheduling.Task task, String lastMessage, Date scheduledNextExecution) {
            this.task = task;
            this.lastMessage = lastMessage;
            this.scheduledNextExecution = scheduledNextExecution;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public int getNrofInvocations() {
            return nrofInvocations;
        }

        public int getNrofFailedInvocations() {
            return nrofFailedInvocations;
        }

        public String toString() {
            return "next task " + task.getClass().getSimpleName() + " scheduled at " + scheduledNextExecution + ", last message: " + lastMessage;
        }
    }

    private Map<cloud.qasino.quiz.scheduling.Task, ScheduledTask> taskDescriptions = new HashMap<>();

    public ScheduledTask scheduleTask(final Task task, final int interval, final TimeUnit timeUnit) {

        final ScheduledTask taskDescription = new ScheduledTask(task, "task not yet executed", new Date(System.currentTimeMillis() + (timeUnit.toMillis(interval))));
        taskDescriptions.put(task, taskDescription);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("about to run: " + taskDescription);
                try {
                    taskDescription.lastMessage = task.execute();
                } catch (Exception e) {
                    log.error("task execution failed for task: " + taskDescription, e);
                    taskDescription.lastMessage = e.getMessage();
                    taskDescription.nrofFailedInvocations++;
                } finally {
                    taskDescription.nrofInvocations++;
                }
                taskDescription.scheduledNextExecution = new Date(System.currentTimeMillis() + (timeUnit.toMillis(interval)));
                scheduler.schedule(this, interval, timeUnit);
                log.info("done running: " + taskDescription);
            }
        }, interval, timeUnit);
        return taskDescription;
    }
}
