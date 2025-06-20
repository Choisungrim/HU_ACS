package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.NodeMaster;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.vo.NodeMasterId;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.position.cache.RobotPositionCache;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Point;
import com.hubis.acs.common.position.model.Position;
import com.hubis.acs.common.utils.PositionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("middleware_position_change")
public class PositionChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(PositionChange.class);

    @Autowired
    private RobotPositionCache robotPositionCache; // 로봇별 마지막 위치 저장소 (선택)

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        double x = message.getDouble("x");
        double y = message.getDouble("y");
        double deg = message.getDouble("deg");

        if (Double.isNaN(x) || Double.isNaN(y)) {
            logger.warn("[{}] Invalid position data: x={}, y={}", robotId, x, y);
            return BaseConstants.RETURNCODE.Fail;
        }

        Position newPos = new Position(x, y, deg);
        RobotMasterId masterId = new RobotMasterId(robotId,eventInfo.getSiteId());
        RobotMaster robot = baseService.findById(RobotMaster.class, masterId);

        if(robot == null) { logger.warn("robot not found");  return BaseConstants.RETURNCODE.Fail;}

        long mapuuid = robot.getMap_uuid();
        // 이전 위치 가져오기
        Position prevGlobalPos = robotPositionCache.get(robotId);

        // Local → Global 변환 적용
        Point globalPoint = mapTransformManager.toGlobal(robot.getModel_nm(), PositionUtils.toPoint(newPos));
        Position globalPos = PositionUtils.toPosition(globalPoint, newPos.getTheta());
        robotPositionCache.put(robotId, globalPos);

        // 목적지 기반 경로 블록 확인
        List<Position> path = generateSurroundingPath(globalPos, 100.0, 10.0);

        String destId = processManager.getCurrentDestination(robotId);
        if (destId != null) {
            NodeMasterId nodeId = new NodeMasterId(destId,mapuuid,eventInfo.getSiteId());
            NodeMaster destPos = baseService.findById(NodeMaster.class, nodeId);
            logger.debug("[{}] Checking path to dest: {} → {}", robotId, globalPos, destPos);
            //목적지가 있는경우
        }
        else
        {
            //목적지가 없는경우
            logger.warn("[{}] at ({},{}) is destination is null", robotId, x, y);
        }

        // 경로 상 블록 여부 확인
        if (pathValidator.isPathBlocked(eventInfo.getSiteId(), mapuuid, path, robotId)) {
            logger.warn("[{}] Path is blocked at pos {}", robotId, globalPos);
            // 추후: 정지 명령 또는 회피로직 삽입
        }
        
        // zone 점유
        updateZoneOccupancy(mapuuid, prevGlobalPos, globalPos, robotId);

        writerService.sendToUIPositionChange(eventInfo, BaseConstants.RETURNCODE.Success, globalPos);

        return result;
    }

    private List<Position> generateSurroundingPath(Position center, double radius, double step) {
        List<Position> path = new ArrayList<>();
        double startX = center.getX() - radius;
        double endX = center.getX() + radius;
        double startY = center.getY() - radius;
        double endY = center.getY() + radius;

        for (double x = startX; x <= endX; x += step) {
            for (double y = startY; y <= endY; y += step) {
                path.add(new Position(x, y, 0));
            }
        }
        return path;
    }

    private void updateZoneOccupancy(long mapuuid, Position prevPos, Position currPos, String robotId) {
        boolean isFirst = (prevPos == null);
        var zones = globalZoneManager.getZonesByMap(mapuuid);

        for (GlobalZone zone : zones) {
            boolean wasIn = !isFirst && zone.contains(prevPos);
            boolean isIn = zone.contains(currPos);

            if (isFirst && isIn) {
                if (zoneLockManager.lock(eventInfo.getSiteId(), zone.getZoneId(), robotId)) {
                    logger.info("[{}] FIRST entry into zone [{}]", robotId, zone.getZoneId());
                }
            } else if (!wasIn && isIn) {
                if (zoneLockManager.lock(eventInfo.getSiteId(), zone.getZoneId(), robotId)) {
                    logger.info("[{}] Entered zone [{}]", robotId, zone.getZoneId());
                }
            } else if (wasIn && !isIn) {
                zoneLockManager.release(eventInfo.getSiteId(), zone.getZoneId(), robotId);
                logger.info("[{}] Exited zone [{}]", robotId, zone.getZoneId());
            }
        }
    }
}

