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
        Map<String, GlobalWorkHandlerIF> beans = context.getBeansOfType(GlobalWorkHandlerIF.class);
        for (Map.Entry<String, GlobalWorkHandlerIF> entry : beans.entrySet()) {
            handlerMap.put(entry.getKey(), entry.getValue());
        }
        System.out.println("🔧 Registered Handlers: " + handlerMap.keySet());
    }

    public static GlobalWorkHandlerIF getHandler(String group, String workId) {
        String key = group + "_" + workId.toLowerCase();
        if (!handlerMap.containsKey(key)) {
            return null;
        }
        return handlerMap.get(group + "_" + workId.toLowerCase());
    }
}

