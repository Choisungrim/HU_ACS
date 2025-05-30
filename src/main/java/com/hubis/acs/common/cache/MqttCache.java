package com.hubis.acs.common.cache;

import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component("MqttCache")
public class MqttCache {
    private final Logger logger = LoggerFactory.getLogger(MqttCache.class);

    private ConcurrentHashMap<String, HashMap<String, Object>> mqttVehicleInfo;
    public HashMap<String, BlockingQueue<Message<?>>> commandVehicleQueue;
    public Queue<JSONObject> command;


    @PostConstruct
    public void initialize()
    {
        try
        {
            mqttVehicleInfo= new ConcurrentHashMap<String, HashMap<String,Object>>();
            commandVehicleQueue = new HashMap<String, BlockingQueue<Message<?>>>();
            command = new ConcurrentLinkedQueue<>();
            logger.info("MqttCache Initialized");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());

            logger.error("MqttCache Load Error: " + e.getMessage());
        }
    }

    public void addMqttVehicle(String vehicleId, String key, Object value) {
        if (!mqttVehicleInfo.containsKey(vehicleId)) {
            mqttVehicleInfo.put(vehicleId, new HashMap<String, Object>());
        }
        mqttVehicleInfo.get(vehicleId).put(key, value);
    }

    public Object getMqttVehicleByKey(String vehicleId, String key) {
        if (mqttVehicleInfo.get(vehicleId) != null && mqttVehicleInfo.get(vehicleId).containsKey(key))
            return mqttVehicleInfo.get(vehicleId).get(key);

        return null;
    }

    public HashMap<String, Object> getMqttVehicle( String strVehicleId )
    {
        if(!mqttVehicleInfo.containsKey(strVehicleId))
            return new HashMap<String,Object>();

        return mqttVehicleInfo.get(strVehicleId);
    }


    public HashMap<String, BlockingQueue<Message<?>>> getMqttVehicleQueue( String strVehicleId )
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            return new HashMap<String, BlockingQueue<Message<?>>>();

        return commandVehicleQueue;
    }

    public BlockingQueue<Message<?>> getMqttVehicleQueuePoll(String robotId) {
        return commandVehicleQueue.computeIfAbsent(robotId, k -> new LinkedBlockingQueue<>(1000));
    }


    public void addMqttVehicleQueue( String strVehicleId , Message<?> command)
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            commandVehicleQueue.put(strVehicleId, new LinkedBlockingQueue<>(1000));

        commandVehicleQueue.get(strVehicleId).add(command);
    }

    public void removeMqttVehicleQueue( String strVehicleId )
    {
        if(!mqttVehicleInfo.containsKey(strVehicleId))
            return;

        commandVehicleQueue.remove(strVehicleId);
    }

    public void updatePingState(String robotId, String tid) {
        HashMap<String, Object> info = mqttVehicleInfo.computeIfAbsent(robotId, k -> new HashMap<>());
        Integer count = (Integer) info.getOrDefault("connectionCount", 10);
        info.put("connectionCount", Math.max(0, count - 1)); // 1씩 감소
        info.put("heartbeat_tid", tid); // 1씩 감소
    }

    public void initializeSite(String robotId, String siteCd) {
        HashMap<String, Object> info = mqttVehicleInfo.computeIfAbsent(robotId, k -> new HashMap<>());
        info.put("siteId", siteCd); // 1씩 감소
    }

}
