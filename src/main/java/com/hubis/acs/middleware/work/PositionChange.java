package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.NodeMaster;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.vo.NodeMasterId;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.position.cache.RobotPositionCache;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Position;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("middleware_position_change")
public class PositionChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(PositionChange.class);

    @Autowired
    private RobotPositionCache robotPositionCache; // 로봇별 마지막 위치 저장소 (선택)

    @Override
    public String doWork(JSONObject message) throws Exception {
        System.out.println("working PositionChange");

        String robotId = eventInfo.getUserId();
        double x = message.getDouble("x");
        double y = message.getDouble("y");
        double deg = message.getDouble("deg");

        Position newPos = new Position(x, y, deg);

        // 이전 위치 가져오기
        Position prevPos = robotPositionCache.get(robotId);
        robotPositionCache.put(robotId, newPos);

        //map 가져오기
        RobotMasterId masterId = new RobotMasterId(robotId,eventInfo.getSiteId());
        RobotMaster robot = baseService.findById(RobotMaster.class, masterId);

        if(robot == null) { logger.warn("robot not found");  return BaseConstants.RETURNCODE.Fail;}
        long mapuuid = robot.getMap_uuid();

        // 목적지 기반 경로 블록 확인
        String destId = processManager.getCurrentDestination(robotId);
        NodeMasterId nodeId = new NodeMasterId(destId,mapuuid,eventInfo.getSiteId());

        for (GlobalZone z : globalZoneManager.getZonesByMap(mapuuid)) {
            System.out.println("Zone loaded: " + z.getZoneId() + ", x: [" + z.getMinX() + "," + z.getMaxX() + "], y: [" + z.getMinY() + "," + z.getMaxY() + "]");
        }

        if (destId != null) {
            NodeMaster destPos = baseService.findById(NodeMaster.class, nodeId); // 또는 GoalMaster 등으로부터 Position을 추출
            List<Position> path = generateSurroundingPath(newPos, 2.0, 1.0);

            //점유 여부 검사
            if (pathValidator.isPathBlocked(eventInfo.getSiteId(), mapuuid, path, robotId)) {
                logger.warn("Blocked path detected for robot {} from {} to {}", robotId, newPos, destPos);
                // 대기 또는 회피 트리거 삽입 가능
                return result;
            }

            //점유 수행
            for (GlobalZone zone : globalZoneManager.getZonesByMap(mapuuid)) {

                boolean firstPosition = (prevPos == null);
                boolean wasInZone = !firstPosition && zone.contains(prevPos);
                boolean isInZone = zone.contains(newPos);

                if (firstPosition && isInZone) {
                    boolean locked = zoneLockManager.lock(eventInfo.getSiteId(), zone.getZoneId(), robotId);
                    logger.info("[{}] FIRST POSITION - entered zone [{}], locked = {}", robotId, zone.getZoneId(), locked);
                } else if (!wasInZone && isInZone) {
                    boolean locked = zoneLockManager.lock(eventInfo.getSiteId(), zone.getZoneId(), robotId);
                    logger.info("[{}] entered zone [{}], locked = {}", robotId, zone.getZoneId(), locked);
                } else if (wasInZone && !isInZone) {
                    zoneLockManager.release(eventInfo.getSiteId(), zone.getZoneId(), robotId);
                    logger.info("[{}] exited zone [{}]", robotId, zone.getZoneId());
                }
                else {
                    logger.info("[{}] This zone has not been locked", robotId);
                }
            }

            // optional: blocked 영역 진입 여부
            boolean isBlocked = globalZoneManager.isPositionBlocked(newPos, mapuuid);
            logger.debug("[{}] at ({},{}) isBlocked: {}", robotId, x, y, isBlocked);
        }
        else logger.warn("[{}] at ({},{}) is destination is null", robotId, x, y);

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


}

