package com.hubis.acs.middleware;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.configuration.thread.RobotWorkerThread;
import com.hubis.acs.common.handler.BaseExecutorHandler;
import com.hubis.acs.common.utils.JsonUtils;
import com.hubis.acs.process.ProcessNotifyService;
import com.hubis.acs.service.RobotService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class MiddlewareMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(MiddlewareMessageHandler.class);
    private static final DateTimeFormatter TID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private final Map<String, Thread> workerThreads = new ConcurrentHashMap<>();

    private final BaseExecutorHandler executor;
    private final MqttCache mqttCache;
    private final RobotService robotService;
    private final ProcessNotifyService processNotifyService;

    public void handle(String topic, Message<?> message)
    {
        String[] parts = topic.split("/");

        if (parts.length < 4) {
            logger.warn("Invalid topic format: {}", topic);
            return;
        }

        String category = parts[2]; // connection, state, task
        String robotId = parts[1];

        if(category.equals("connection"))
            processMiddleWareHeartbeatMessage(message, robotId);
        else
            processMiddleWareMessages(message, robotId);

    }

    public void processMiddleWareHeartbeatMessage(Message<?> message, String robotId)
    {
        try {
            JSONObject reqMsg = JsonUtils.validationMessageToJsonObject(message);

            ConcurrentHashMap<String, Object> vehicleInfo = mqttCache.getMqttVehicle(robotId);

            String tid = reqMsg.optString("tid");
            String sentTid = (String) vehicleInfo.getOrDefault("heartbeat_tid", tid);

            long rtt = calculate(sentTid, tid);

            String siteId = (String)vehicleInfo.getOrDefault("siteId","HU");

            mqttCache.addMqttVehicle(robotId, "response_tid", tid);
            mqttCache.addMqttVehicle(robotId, "rtt", rtt);
            mqttCache.addMqttVehicle(robotId, "connectionCount", 10);

            robotService.refreshRobotConnectionStatus(robotId,siteId);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void processMiddleWareMessages(Message<?> message, String robotId)
    {
        logger.info("received : {}",message.getPayload());
        mqttCache.addMqttVehicleQueue(robotId, message);

        handleMessageStartWorker(robotId);

    }

    private void handleMessageStartWorker(String robotId)
    {
        if (workerThreads.containsKey(robotId)) return;

        RobotWorkerThread worker = new RobotWorkerThread(robotId, executor, mqttCache, processNotifyService);
        Thread thread = new Thread(worker, "Worker-" + robotId);
        thread.start();

        workerThreads.put(robotId, thread);
    }

    private long calculate(String reqTid, String resTid)
    {
        try {
            LocalDateTime sentTime = LocalDateTime.parse(reqTid, TID_FORMAT);
            LocalDateTime recvTime = LocalDateTime.parse(resTid, TID_FORMAT);
            long sentMillis = sentTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            long recvMillis = recvTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            long res = recvMillis - sentMillis;
            if(res <= 0)
                return 0;
            else
                return res;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
