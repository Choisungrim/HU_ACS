package com.hubis.acs.common.position.adapter.impl;

import com.hubis.acs.common.position.adapter.PositionProvider;
import com.hubis.acs.common.position.model.Position;
import com.hubis.acs.common.position.model.Point;
import com.hubis.acs.common.position.transform.MapTransform;

import java.util.HashMap;
import java.util.Map;

/**
 * Omron 로봇 다중 운영을 고려한 동적 위치 Provider
 */
public class OmronPositionProvider implements PositionProvider {

    // 로봇 ID별 현재 로컬 위치, heading 정보
    private final Map<String, Point> localPositionMap = new HashMap<>();
    private final Map<String, Double> headingMap = new HashMap<>();

    // 로봇 ID별 MapTransform 정보
    private final Map<String, MapTransform> transformMap = new HashMap<>();

    @Override
    public Position getCurrentPosition(String robotId) {
        Point local = localPositionMap.get(robotId);
        Double heading = headingMap.get(robotId);
        MapTransform transform = transformMap.get(robotId);

        if (local == null || heading == null || transform == null) {
            return null; // 또는 예외 throw
        }

        Point global = transform.toGlobal(local);
        return new Position(global.getX(), global.getY(), heading);
    }

    @Override
    public Position getPositionFromNode(String nodeId) {
        // 실제로는 DB or NodeRegistry로 구현
        return nodeMap.get(nodeId);
    }

    @Override
    public Position getGoalPosition(String goalId) {
        return goalMap.get(goalId);
    }

    // 외부에서 주기적으로 갱신 (MQTT, API 등)
    public void updateLocalPosition(String robotId, double x, double y, double thetaDeg) {
        localPositionMap.put(robotId, new Point(x, y));
        headingMap.put(robotId, thetaDeg);
    }

    public void registerTransform(String robotId, Point origin, double rotationDeg, double resolution) {
        transformMap.put(robotId, new MapTransform(origin, rotationDeg, resolution));
    }

    // 노드/Goal 위치 임시 예시 (실제는 Registry 분리 권장)
    private final Map<String, Position> nodeMap = new HashMap<>();
    private final Map<String, Position> goalMap = new HashMap<>();

    public void registerNode(String nodeId, Position pos) {
        nodeMap.put(nodeId, pos);
    }

    public void registerGoal(String goalId, Position pos) {
        goalMap.put(goalId, pos);
    }
}


