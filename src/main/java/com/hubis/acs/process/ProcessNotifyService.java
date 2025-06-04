package com.hubis.acs.process;

import com.hubis.acs.common.entity.vo.EventInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 예시: ProcessNotifyService
@Service
public class ProcessNotifyService {
    private final Map<String, ExecutorService> robotExecutors = new ConcurrentHashMap<>();


    private final ProcessManager processManager;

    public ProcessNotifyService(ProcessManager processManager) {
        this.processManager = processManager;
    }

    private ExecutorService getExecutor(String robotId) {
        return robotExecutors.computeIfAbsent(robotId, id -> Executors.newSingleThreadExecutor(r -> new Thread(r, "Executor-" + id)));
    }

    public void notifyResponse(String txId, String robotId) {
        getExecutor(robotId).submit(() -> {
            processManager.notifyResponse(txId);  // latch down만
        });
    }

    public void notifyState(String txId, EventInfo info, JSONObject reqMsg, String robotId) {
        getExecutor(robotId).submit(() -> {
            processManager.addEvent(info, reqMsg); // 비즈니스 실행
            processManager.notifyState(txId, reqMsg.optString("task_status"));       // latch down
        });
    }
}

