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

    // 1. Create a new task using milliseconds
    public void scheduleElectionTask(String taskId, long timeEpochMillis, Boolean isStartTime, Runnable taskLogic) {
        cancelTask(taskId); // Cancel if already exists

        long adjustedTime;

        if(isStartTime){
            adjustedTime = timeEpochMillis - FIFTEEN_MINUTES_IN_MILLIS;
        }else{
            adjustedTime = timeEpochMillis;
        }
        long delay = calculateDelayFromNow(adjustedTime);

        //for only testing purposes
        if(isStartTime){
            delay = 0;
        }

        if (delay < 0) {
            throw new IllegalArgumentException("Scheduled time is in the past after adjustment.");
        }

        ScheduledFuture<?> future = scheduler.schedule(taskLogic, delay, TimeUnit.MILLISECONDS);
        taskMap.put(taskId, future);

        log.debug("Scheduled task '" + taskId + "' for " + formatEpochMillis(adjustedTime));
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
