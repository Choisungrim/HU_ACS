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
    private static final Logger logger = LoggerFactory.getLogger(MqttCache.class);

    private static ConcurrentHashMap<String, HashMap<String, Object>> mqttVehicleInfo;
    public static HashMap<String, BlockingQueue<Message<?>>> commandVehicleQueue;
    public static Queue<JSONObject> command;


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

    public static void addMqttVehicle(String vehicleId, String key, Object value) {
        if (!mqttVehicleInfo.containsKey(vehicleId)) {
            mqttVehicleInfo.put(vehicleId, new HashMap<String, Object>());
        }
        mqttVehicleInfo.get(vehicleId).put(key, value);
    }

    public static Object getMqttVehicle(String vehicleId, String key) {
        if (mqttVehicleInfo.get(vehicleId) != null && mqttVehicleInfo.get(vehicleId).containsKey(key))
            return mqttVehicleInfo.get(vehicleId).get(key);

        return null;
    }

    public static HashMap<String, Object> getMqttVehicle( String strVehicleId )
    {
        if(!mqttVehicleInfo.containsKey(strVehicleId))
            return new HashMap<String,Object>();

        return mqttVehicleInfo.get(strVehicleId);
    }


    public static HashMap<String, BlockingQueue<Message<?>>> getMqttVehicleQueue( String strVehicleId )
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            return new HashMap<String, BlockingQueue<Message<?>>>();

        return commandVehicleQueue;
    }

    public static BlockingQueue<Message<?>> getMqttVehicleQueuePoll( String strVehicleId )
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            return new LinkedBlockingQueue<>(1000);

        return commandVehicleQueue.get(strVehicleId);
    }

    public static void addMqttVehicleQueue( String strVehicleId , Message<?> command)
    {
        if(!commandVehicleQueue.containsKey(strVehicleId))
            commandVehicleQueue.put(strVehicleId, new LinkedBlockingQueue<>(1000));

        commandVehicleQueue.get(strVehicleId).add(command);
    }

    public static void removeMqttVehicleQueue( String strVehicleId )
    {
        if(!mqttVehicleInfo.containsKey(strVehicleId))
            return;

        commandVehicleQueue.remove(strVehicleId);
    }
}
