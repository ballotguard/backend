package io.github.ballotguard.services.vote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class TaskSchedulerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    private static final long FIFTEEN_MINUTES_IN_MILLIS = 15 * 60 * 1000;
    private static final long THIRTEEN_MINUTES_IN_MILLIS = 13 * 60 * 1000;

    // 1. Create a new task using milliseconds
    public void scheduleElectionTask(String taskId, long timeEpochMillis, Boolean isStartTime, Runnable taskLogic) {
        cancelTask(taskId); // Cancel if already exists

        long now = System.currentTimeMillis();
        long scheduledTimeMillis;

        if (isStartTime) {
            long timeUntilStart = timeEpochMillis - now;

            // If election starts at least 15 minutes from now,
            // send links exactly 13 minutes before start.
            if (timeUntilStart >= FIFTEEN_MINUTES_IN_MILLIS) {
                scheduledTimeMillis = timeEpochMillis - THIRTEEN_MINUTES_IN_MILLIS;
            } else {
                // If election starts in less than 15 minutes,
                // send links immediately.
                scheduledTimeMillis = now;
            }
        } else {
            // For non-start-time tasks (e.g. results), schedule at given time.
            scheduledTimeMillis = timeEpochMillis;
        }

        long delay = scheduledTimeMillis - now;

        if (delay < 0) {
            throw new IllegalArgumentException("Scheduled time is in the past after adjustment.");
        }

        ScheduledFuture<?> future = scheduler.schedule(taskLogic, delay, TimeUnit.MILLISECONDS);
        taskMap.put(taskId, future);

        log.debug("Scheduled task '" + taskId + "' for " + formatEpochMillis(scheduledTimeMillis));
    }


    // 3. Delete task
    public void cancelTask(String taskId) {
        ScheduledFuture<?> future = taskMap.remove(taskId);
        if (future != null) {
            future.cancel(false); // Don't interrupt if running
            log.debug("Cancelled task: " + taskId);
        }
    }

    // Helper: compute delay from now
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
