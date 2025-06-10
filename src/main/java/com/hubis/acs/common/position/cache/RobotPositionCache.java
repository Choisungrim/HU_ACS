package com.hubis.acs.common.position.cache;

import com.hubis.acs.common.position.model.Position;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RobotPositionCache {
    private final Map<String, Position> map = new ConcurrentHashMap<>();

    public Position get(String robotId) {
        return map.get(robotId);
    }

    public void put(String robotId, Position pos) {
        map.put(robotId, pos);
    }
}

