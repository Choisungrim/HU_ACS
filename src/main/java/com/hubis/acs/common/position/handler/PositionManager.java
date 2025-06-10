package com.hubis.acs.common.position.handler;

import com.hubis.acs.common.position.adapter.PositionProvider;
import com.hubis.acs.common.position.model.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * 로봇 ID 기반으로 PositionProvider를 위임하여 위치 정보를 통합 관리
 */
public class PositionManager {

    // 로봇 ID → PositionProvider 매핑
    private final Map<String, PositionProvider> providerMap = new HashMap<>();

    /**
     * 로봇 ID에 대해 적절한 Provider를 등록
     */
    public void register(String robotId, PositionProvider provider) {
        providerMap.put(robotId, provider);
    }

    /**
     * 로봇의 현재 위치 조회
     */
    public Position getCurrentPosition(String robotId) {
        PositionProvider provider = providerMap.get(robotId);
        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for robot: " + robotId);
        }
        return provider.getCurrentPosition(robotId);
    }

    /**
     * 로봇의 노드 ID에 해당하는 위치 조회
     */
    public Position getPositionFromNode(String robotId, String nodeId) {
        PositionProvider provider = providerMap.get(robotId);
        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for robot: " + robotId);
        }
        return provider.getPositionFromNode(nodeId);
    }

    /**
     * 로봇의 Goal ID에 해당하는 위치 조회
     */
    public Position getGoalPosition(String robotId, String goalId) {
        PositionProvider provider = providerMap.get(robotId);
        if (provider == null) {
            throw new IllegalArgumentException("No provider registered for robot: " + robotId);
        }
        return provider.getGoalPosition(goalId);
    }
}

