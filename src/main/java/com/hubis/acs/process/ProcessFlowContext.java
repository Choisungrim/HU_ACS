package com.hubis.acs.process;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.service.WriterService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Getter
public class ProcessFlowContext {

    private static final Logger logger = LoggerFactory.getLogger(ProcessFlowContext.class);
    private final String processId;
    private final String robotId;
    private final MqttCache mqttCache;
    private final WriterService writerService;
    private volatile String currentDestination;
    private volatile TaskType currentTask;

    private final Map<String, Map<String, String>> taskMap = new ConcurrentHashMap<>();

    private final Map<String, CountDownLatch> responseLatches = new ConcurrentHashMap<>();
    private final Map<String, CountDownLatch> stateLatches = new ConcurrentHashMap<>();

    public ProcessFlowContext(String processId, String robotId, MqttCache mqttCache, WriterService writerService) {
        this.processId = processId;
        this.robotId = robotId;
        this.mqttCache = mqttCache;
        this.writerService = writerService;
    }

    public void setCurrentTask(TaskType task, String dest) {
        this.currentTask = task;
        this.currentDestination = dest;
    }

    public void prepareTask(TaskType task, String txId) {
        Map<String, String> taskInfo = taskMap.computeIfAbsent(processId, k -> new ConcurrentHashMap<>());
        taskInfo.put(TaskType.TRANSACTION_ID.name(), txId);
        taskInfo.put(TaskType.TASK.name(), task.name());
        responseLatches.put(txId, new CountDownLatch(1));
        stateLatches.put(txId, new CountDownLatch(1));

        System.out.println( taskMap.toString() );
    }

    public void sendTask(TaskType task, String destination, String txId) {
        taskMap.computeIfAbsent(processId, k -> new ConcurrentHashMap<>()).put(TaskType.DESTINATION.name(), destination);
        if (!responseLatches.containsKey(txId) || !stateLatches.containsKey(txId)) {
            logger.warn("Latch not prepared before sendTask for txId={}", txId);
        }
        logger.info("Sending task: {} with txId={} for robot={} (processId={})", task, txId, robotId, processId);

        setCurrentTask(task,destination);

        StringBuilder topics = new StringBuilder();
        topics.append(BaseConstants.TAG_NAME.MiddleWare + "/");
        topics.append(robotId+ "/");
        topics.append(BaseConstants.TAG_NAME.Task + "/" + BaseConstants.TAG_NAME.Request);
        writerService.sendToJsonMiddleware(txId, task.name().toLowerCase(), topics.toString(), processId, robotId, destination);
    }

    public boolean awaitResponse(TaskType task, int timeoutSeconds) {
        String txId = taskMap.get(processId).get(TaskType.TRANSACTION_ID.name());
        CountDownLatch latch = responseLatches.get(txId);
        logger.info("awaitResponse txId={} timeoutSeconds = {}",txId, timeoutSeconds);
        try {
            return latch != null && latch.await(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while awaiting response for task: {}", task);
            return false;
        } finally {
            responseLatches.remove(txId); // 💡 latch 정리
        }
    }

    public boolean awaitStateComplete(TaskType task, int timeoutMinutes) {
        String txId = taskMap.get(processId).get(TaskType.TRANSACTION_ID.name());
        CountDownLatch latch = stateLatches.get(txId);
        logger.info("awaitComplete txId={} timeoutMinutes = {}",txId, timeoutMinutes);
        try {
            return latch != null && latch.await(timeoutMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while awaiting state complete for task: {}", task);
            return false;
        } finally {
            stateLatches.remove(txId); // 💡 latch 정리
        }
    }

    public void tryNotifyResponse(String txId) {
        CountDownLatch latch = responseLatches.get(txId);
        if (latch != null) {
            latch.countDown();
            logger.info("Response latch released for txId={}", txId);
        } else {
            logger.warn("No latch found for txId={}", txId);
        }
    }

    public void tryNotifyState(String txId, String status) {
        if(TaskState.COMPLETE.name().equalsIgnoreCase(status.toUpperCase())){
            CountDownLatch latch = stateLatches.get(txId);
            if (latch != null) {
                latch.countDown();
                logger.info("State latch released for txId={}", txId);
            } else {
                logger.warn("No latch found for txId={}", txId);
            }
        }
        else logger.warn("No COMPLETE latch found for txId={}", txId);
    }


    public String getCurrentDestination() {
        return currentDestination;
    }

    public TaskType getCurrentTask() {
        return currentTask;
    }
}
