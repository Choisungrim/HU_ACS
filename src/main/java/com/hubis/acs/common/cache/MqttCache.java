package com.hubis.acs.common.cache;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component("MqttCache")
public class MqttCache {
    private final Logger logger = LoggerFactory.getLogger(MqttCache.class);

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> mqttVehicleInfo;
    public ConcurrentHashMap<String, BlockingQueue<Message<?>>> commandVehicleQueue;
    public Queue<JSONObject> command;


    @PostConstruct
    public void initialize()
    {
        try
        {
            mqttVehicleInfo= new ConcurrentHashMap<String, ConcurrentHashMap<String,Object>>();
            commandVehicleQueue = new ConcurrentHashMap<String, BlockingQueue<Message<?>>>();
            command = new ConcurrentLinkedQueue<>();
            logger.info("MqttCache Initialized");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());

            logger.error("MqttCache Load Error: " + e.getMessage());
        }
    }

    public synchronized void addMqttVehicle(String vehicleId, String key, Object value) {
        if (!mqttVehicleInfo.containsKey(vehicleId)) {
            mqttVehicleInfo.put(vehicleId, new ConcurrentHashMap<String, Object>());
        }
        mqttVehicleInfo.get(vehicleId).put(key, value);
    }

    public Object getMqttVehicleByKey(String vehicleId, String key) {
        if (mqttVehicleInfo.get(vehicleId) != null && mqttVehicleInfo.get(vehicleId).containsKey(key))
            return mqttVehicleInfo.get(vehicleId).get(key);

        return null;
    }

    public ConcurrentHashMap<String, Object> getMqttVehicle( String strVehicleId )
    {
        return mqttVehicleInfo.computeIfAbsent(strVehicleId, k -> new ConcurrentHashMap<>());
    }

    public Map<String, Map<String, Object>> getMqttCacheInfo() {
        // 깊은 복사 없이 읽기 전용 Map 반환
        Map<String, Map<String, Object>> result = new HashMap<>();
        for (String key : mqttVehicleInfo.keySet()) {
            result.put(key, Map.copyOf(mqttVehicleInfo.get(key)));
        }
        return Map.copyOf(result);
    }


    public ConcurrentHashMap<String, BlockingQueue<Message<?>>> getMqttVehicleQueue( String strVehicleId )
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            return new ConcurrentHashMap<String, BlockingQueue<Message<?>>>();

        return commandVehicleQueue;
    }

    public BlockingQueue<Message<?>> getMqttVehicleQueuePoll(String strVehicleId) {
        return commandVehicleQueue.computeIfAbsent(strVehicleId, k -> new LinkedBlockingQueue<>(1000));
    }


    public void addMqttVehicleQueue( String strVehicleId , Message<?> command)
    {
        commandVehicleQueue
                .computeIfAbsent(strVehicleId, k -> new LinkedBlockingQueue<>(1000))
                .add(command);
    }

    public void removeMqttVehicleQueue( String strVehicleId )
    {
        if(!mqttVehicleInfo.containsKey(strVehicleId))
            return;

        commandVehicleQueue.remove(strVehicleId);
    }

    public void updatePingState(String robotId, String tid) {
        mqttVehicleInfo.compute(robotId, (id, info) -> {
            if (info == null) info = new ConcurrentHashMap<>();
            Integer count = (Integer) info.getOrDefault("connectionCount", 10);
            info.put("connectionCount", Math.max(0, count - 1));
            info.put("heartbeat_tid", tid);
            return info;
        });
    }


    public void initializeSite(String robotId, String siteCd) {
        ConcurrentHashMap<String, Object> info = mqttVehicleInfo.computeIfAbsent(robotId, k -> new ConcurrentHashMap<>());
        info.put("siteId", siteCd); // 1씩 감소
    }

}
