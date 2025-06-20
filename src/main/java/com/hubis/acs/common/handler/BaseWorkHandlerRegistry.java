package com.hubis.acs.common.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BaseWorkHandlerRegistry {
    @Autowired
    private ApplicationContext context;

    private static final Map<String, GlobalWorkHandlerIF> handlerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        System.out.println("🛠 BaseWorkClassLoader Initialized");

        try {
            Map<String, GlobalWorkHandlerIF> beans = context.getBeansOfType(GlobalWorkHandlerIF.class);
            for (Map.Entry<String, GlobalWorkHandlerIF> entry : beans.entrySet()) {
                String key = entry.getKey();
                try {
                    handlerMap.put(key, entry.getValue());
                } catch (Exception handlerEx) {
                    System.err.println("❌ Failed to register handler: " + key);
                    handlerEx.printStackTrace();
                }
            }
            System.out.println("🔧 Final Registered Handlers: " + handlerMap.keySet());

        } catch (Exception e) {
            System.err.println("❗ Handler Registry Init Failed");
            e.printStackTrace();
        }
    }

    public static GlobalWorkHandlerIF getHandler(String group, String workId) {
        String key = group + "_" + workId.toLowerCase();
        if (!handlerMap.containsKey(key)) {
            return null;
        }
        return handlerMap.get(group + "_" + workId.toLowerCase());
    }
}

