// ProcessManager.java
package com.hubis.acs.process;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.BaseExecutorHandler;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.RuntimeUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.service.BaseService;
import com.hubis.acs.service.WriterService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProcessManager {

    private static final Logger logger = LoggerFactory.getLogger(ProcessManager.class);
    private final MqttCache mqttCache;
    private final WriterService writerService;
    private final BaseService baseService;
    private final BaseExecutorHandler executor;
    private final Map<String, ExecutorService> robotExecutors = new ConcurrentHashMap<>();
    private final Map<String, ProcessFlowContext> processMap = new ConcurrentHashMap<>();
    private final Map<String, EventInfo> eventInfoMap = new ConcurrentHashMap<>();
    private final Map<String, JSONObject> reqMsgMap = new ConcurrentHashMap<>();
    private final Set<String> runningRobots = ConcurrentHashMap.newKeySet();
    private static final int MAX_RETRY = 3;
    private static final int RESPONSE_TIMEOUT_SEC = 5;
    private static final int STATE_TIMEOUT_MIN = 30;

    public void tryStartProcess(String processId, String robotId, String siteId, String source, String dest) {

        ExecutorService executor = robotExecutors.computeIfAbsent(
                robotId,
                key -> Executors.newSingleThreadExecutor()
        );
        ProcessFlowContext context = new ProcessFlowContext(processId, robotId, mqttCache, writerService);
        processMap.put(processId, context);

        executor.execute(() -> runProcess(context, source, dest, siteId));
    }

    private void runProcess(ProcessFlowContext ctx, String source, String dest, String siteId) {
        try {
            executeWithRetry(() -> moveTask(ctx, TimeUtils.getCurrentTimekey(), source), BaseConstants.ROBOT.Task.MOVE, ctx);
            executeWithRetry(() -> loadTask(ctx, TimeUtils.getCurrentTimekey(), source), BaseConstants.ROBOT.Task.LOAD, ctx);
            executeWithRetry(() -> moveTask(ctx, TimeUtils.getCurrentTimekey(), dest), BaseConstants.ROBOT.Task.MOVE, ctx);
            executeWithRetry(() -> unLoadTask(ctx, TimeUtils.getCurrentTimekey(), dest), BaseConstants.ROBOT.Task.UNLOAD, ctx);

            jobComplete(ctx, siteId);

            logger.info("Process {} completed for robot {}", ctx.getProcessId(), ctx.getRobotId());
        } catch (Exception e) {
            logger.error("Error in process {}: {}", ctx.getProcessId(), e.getMessage(), e);
        } finally {
            processMap.remove(ctx.getProcessId());
            runningRobots.remove(ctx.getRobotId());
        }
    }

    private void executeWithRetry(Runnable taskAction, String taskName, ProcessFlowContext ctx) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY) {
            try {
                taskAction.run();
                return;
            } catch (Exception e) {
                retryCount++;
                logger.warn("Retry {} for task {} on process {}", retryCount, taskName, ctx.getProcessId());
            }
        }
        throw new RuntimeException("Max retries exceeded for task: " + taskName);
    }

    public void moveTask(ProcessFlowContext ctx, String txId, String destination) {
        ctx.prepareTask(TaskType.MOVE, txId);
        ctx.sendTask(TaskType.MOVE, destination, txId);
        if (!ctx.awaitResponse(TaskType.MOVE, RESPONSE_TIMEOUT_SEC)) {
            throw new RuntimeException("MOVE task response timeout");
        }
        if (!ctx.awaitStateComplete(TaskType.MOVE, STATE_TIMEOUT_MIN)) {
            throw new RuntimeException("MOVE task state complete timeout");
        }
    }

    public void loadTask(ProcessFlowContext ctx, String txId, String destination) {
        ctx.prepareTask(TaskType.LOAD, txId);
        ctx.sendTask(TaskType.LOAD, destination, txId);
        if (!ctx.awaitResponse(TaskType.LOAD, RESPONSE_TIMEOUT_SEC)) {
            throw new RuntimeException("LOAD task response timeout");
        }
        if (!ctx.awaitStateComplete(TaskType.LOAD, STATE_TIMEOUT_MIN)) {
            throw new RuntimeException("LOAD task state complete timeout");
        }
    }

    public void unLoadTask(ProcessFlowContext ctx, String txId, String destination) {
        ctx.prepareTask(TaskType.UNLOAD, txId);
        ctx.sendTask(TaskType.UNLOAD, destination, txId);
        if (!ctx.awaitResponse(TaskType.UNLOAD, RESPONSE_TIMEOUT_SEC)) {
            throw new RuntimeException("UNLOAD task response timeout");
        }
        if (!ctx.awaitStateComplete(TaskType.UNLOAD, STATE_TIMEOUT_MIN)) {
            throw new RuntimeException("UNLOAD task state complete timeout");
        }
    }

    public void jobComplete(ProcessFlowContext ctx, String siteId) {
        try {
            // Job completion logic placeholder
            String processId = ctx.getProcessId();
            TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(processId,siteId));

            //workGroup + _ + workId
            EventInfo eventInfo = new EventInfoBuilder()
                    .addRequestId(BaseConstants.TAG_NAME.ACS)
                    .addWorkId(RuntimeUtils.getCurrentMethodName().toLowerCase())
                    .addWorkGroupId(BaseConstants.TAG_NAME.MiddleWare)
                    .addUserId(transfer.getAssigned_robot_id())
                    .addSiteId(siteId)
                    .build();

            executeHandler(eventInfo, new JSONObject());

        } catch (Exception e) { logger.error("Error in process JOB {}: {}", ctx.getProcessId(), e.getMessage(), e); }
    }

    public void notifyResponse(String transactionId) {
        processMap.values().forEach(ctx -> ctx.tryNotifyResponse(transactionId));
    }

    public void notifyState(String transactionId, String status) {
        if(executeHandler(eventInfoMap.get(transactionId), reqMsgMap.get(transactionId)).equals(BaseConstants.RETURNCODE.Success))
        {
            processMap.values().forEach(ctx -> ctx.tryNotifyState(transactionId, status));

            eventInfoMap.remove(transactionId); // 사용 후 정리
            reqMsgMap.remove(transactionId);
        }
        else
            logger.info("Notify state for transactionId={} status={} not Success", transactionId, status);
    }

    private String executeHandler(EventInfo eventInfo, JSONObject reqMsg)
    {
        return executor.execute(eventInfo, reqMsg, new JSONObject());
    }

    public void addEvent(EventInfo eventInfo, JSONObject reqMsg) {
        String txId = eventInfo.getTransactionId();
        eventInfoMap.put(txId, eventInfo);
        reqMsgMap.put(txId, reqMsg);
    }

    public boolean tryRejectProcess(String processId) {
        ProcessFlowContext ctx = processMap.remove(processId);
        if (ctx != null) {
            runningRobots.remove(ctx.getRobotId());
            logger.warn("Forcefully rejected process {} for robot {}", processId, ctx.getRobotId());
            return true;
        }
        return false;
    }

    public String getCurrentDestination(String robotId) {
        return processMap.values().stream()
                .filter(ctx -> ctx.getRobotId().equals(robotId))
                .map(ProcessFlowContext::getCurrentDestination)
                .findFirst()
                .orElse(null);
    }

    public RobotTaskStatus getCurrentTaskStatus(String robotId) {
        return processMap.values().stream()
                .filter(ctx -> ctx.getRobotId().equals(robotId))
                .map(ctx -> new RobotTaskStatus(ctx.getCurrentTask(), ctx.getCurrentDestination()))
                .findFirst()
                .orElse(null);
    }

    public List<RobotTaskStatus> getAllRobotStatuses() {
        return processMap.values().stream()
                .map(ctx -> new RobotTaskStatus(ctx.getCurrentTask(), ctx.getCurrentDestination()))
                .collect(Collectors.toList());
    }

    public boolean reserveRobot(String robotId) {
        // 이미 실행 중이면 false 반환
        return runningRobots.add(robotId);  // add()는 이미 존재하면 false 반환
    }

    public record RobotTaskStatus(TaskType task, String destination) {}
}


