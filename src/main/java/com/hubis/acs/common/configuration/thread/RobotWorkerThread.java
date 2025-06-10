package com.hubis.acs.common.configuration.thread;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.handler.BaseExecutorHandler;
import com.hubis.acs.common.utils.EventInfoBuilder;
import com.hubis.acs.common.utils.JsonUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.process.ProcessNotifyService;
import org.json.JSONObject;
import org.springframework.messaging.Message;

import java.util.concurrent.BlockingQueue;

public class RobotWorkerThread implements Runnable {

    private final String robotId;
    private final BaseExecutorHandler executor;
    private final MqttCache mqttCache;
    private final ProcessNotifyService processNotifyService;

    public RobotWorkerThread(String robotId, BaseExecutorHandler executor, MqttCache mqttCache, ProcessNotifyService processNotifyService) {
        this.robotId = robotId;
        this.executor = executor;
        this.mqttCache = mqttCache;
        this.processNotifyService = processNotifyService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BlockingQueue<Message<?>> queue = mqttCache.getMqttVehicleQueuePoll(robotId);
                Message<?> msg = queue.take(); // blocking

                JSONObject reqMsg = JsonUtils.validationMessageToJsonObject(msg);
                String topic = msg.getHeaders().get("mqtt_receivedTopic").toString();
                String[] parts = topic.split("/");

                if (parts.length < 4) {
                    System.err.println("Invalid topic format: " + topic);
                    continue;
                }

                String requestId = parts[0];              // "middleware"
                String robotId = parts[1];
                String category = parts[2];               // "task" or "state"
                String subType = parts[3];                // ex: state, mode, etc.

                String siteId = (String) mqttCache.getMqttVehicle(robotId).getOrDefault("siteId", "HU");


                switch (category) {
                    case "task":
                        String subTopics = category+"/"+subType;
                        if(subTopics.equals("task/state"))
                            handleTask(reqMsg, requestId, siteId, robotId);

                        else if (subTopics.equals("task/response"))
                            handleResponse(reqMsg, robotId);

                        break;
                    case "state":
                        EventInfo eventInfo = handleState(reqMsg, requestId, subType, siteId, robotId);
                        executor.execute(eventInfo, reqMsg, new JSONObject());
                        break;
                    default:
                        System.err.println("Unknown category: " + category);
                        break;
                }


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleResponse(JSONObject reqMsg, String robotId) {
        String transactionId = reqMsg.optString("tid", TimeUtils.getCurrentTimekey());

        processNotifyService.notifyResponse(transactionId, robotId);
    }

    private void handleTask(JSONObject reqMsg, String requestId, String siteId, String robotId) {
        String behavior = reqMsg.optString("task_behavior", "unknown");
        String status = reqMsg.optString("task_status", "unknown");
        String transactionId = reqMsg.optString("tid", TimeUtils.getCurrentTimekey());

        String workId;
        if ("running".equalsIgnoreCase(status)) {
            workId = behavior + "_start";
        } else if ("complete".equalsIgnoreCase(status)) {
            workId = behavior + "_complete";
        } else {
            workId = behavior + "_" + status;
        }
        EventInfo eventInfo = new EventInfoBuilder(transactionId)
                .addRequestId(requestId)
                .addWorkId(workId)
                .addWorkGroupId(requestId)
                .addActivity(workId)
                .addUserId(robotId)
                .addSiteId(siteId)
                .build();

        processNotifyService.notifyState(transactionId, eventInfo, reqMsg, robotId);
    }

    private EventInfo handleState(JSONObject reqMsg, String requestId, String subType, String siteId, String robotId) {
        String workId = subType + "_change";
        return new EventInfoBuilder(TimeUtils.getCurrentTimekey())
                .addRequestId(requestId)
                .addWorkId(workId)
                .addWorkGroupId(requestId)
                .addActivity(workId)
                .addUserId(robotId)
                .addSiteId(siteId)
                .build();
    }
}
