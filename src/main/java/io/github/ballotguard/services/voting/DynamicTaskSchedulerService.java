package io.github.ballotguard.services.voting;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class DynamicTaskSchedulerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    // 1. Create a new task
    public void scheduleTask(String taskId, long epochMillis, Runnable taskLogic) {
        cancelTask(taskId); // Cancel if already exists

        long delay = calculateDelayFromNow(epochMillis);
        if (delay < 0) {
            throw new IllegalArgumentException("Scheduled time is in the past.");
        }

        ScheduledFuture<?> future = scheduler.schedule(taskLogic, delay, TimeUnit.MILLISECONDS);
        taskMap.put(taskId, future);

        System.out.println("Scheduled task '" + taskId + "' for " + formatEpochMillis(epochMillis));
    }

    // 2. Edit task (reschedule with new time)
    public void rescheduleTask(String taskId, long newEpochMillis, Runnable taskLogic) {
        System.out.println("Rescheduling task: " + taskId);
        scheduleTask(taskId, newEpochMillis, taskLogic);
    }

    // 3. Delete task
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = taskMap.remove(taskId);
        if (future != null) {
            future.cancel(false); // Don't interrupt if running
            System.out.println("Cancelled task: " + taskId);
        }
    }

    // Helper: compute delay
    private long calculateDelayFromNow(long futureEpochMillis) {
        long now = System.currentTimeMillis();
        return futureEpochMillis - now;
    }

    // Optional: format epochMillis to readable time
    private String formatEpochMillis(long epochMillis) {
        LocalDateTime dateTime = Instant.ofEpochMilli(epochMillis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime();
        return dateTime.toString();
    }
}
