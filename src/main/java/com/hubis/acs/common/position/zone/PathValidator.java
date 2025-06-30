package com.hubis.acs.common.position.zone;

import com.hubis.acs.common.entity.vo.EventInfo;
import com.hubis.acs.common.position.handler.GlobalZoneManager;
import com.hubis.acs.common.position.handler.ZoneLockManager;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PathValidator {
    private final GlobalZoneManager zoneManager;
    private final ZoneLockManager lockManager;

    private static final Logger logger = LoggerFactory.getLogger(PathValidator.class);

    public PathValidator(GlobalZoneManager zoneManager, ZoneLockManager lockManager) {
        this.zoneManager = zoneManager;
        this.lockManager = lockManager;
    }

    /** 경로가 다른 로봇에 의해 점유된 Zone에 닿는지 확인하고, 점유되지 않았다면 내 로봇이 점유 시도 */
    public boolean isPathBlocked(String siteId, long mapuuid, List<Position> path, String robotId) {
        Collection<GlobalZone> zones = zoneManager.getZonesByMap(mapuuid);

        for (GlobalZone zone : zones) {
            Object zoneLock = lockManager.getZoneLock(zone.getZoneId());

            synchronized (zoneLock) {
                if (zone.contains(path)) {
                    String lockOwner = lockManager.getLockOwner(siteId, zone.getZoneId());

                    if (lockOwner == null) {
                        //점유
                        boolean success = lockManager.lock(siteId, zone.getZoneId(), robotId);
                        if (success) {
                            logger.info("[{}] Path-based occupancy: locked zone [{}]", robotId, zone.getZoneId());
                        }
                    } else if (!lockOwner.equals(robotId)) {
                        logger.warn("[{}] Blocked by zone [{}] owned by [{}]", robotId, zone.getZoneId(), lockOwner);
                        return true;
                    }
                    // 내 소유인 경우는 통과
                }
            }
        }
        return false;
    }

    /** 위치 이동에 따라 이전 영역에서 빠져나왔는지 확인하고 점유 해제 */
    public void releaseExitedZones(EventInfo eventInfo, long mapuuid, List<Position> prevPos, List<Position> currPos, String robotId) {
        Collection<GlobalZone> zones = zoneManager.getZonesByMap(mapuuid);
        String siteId = eventInfo.getSiteId();

        for (GlobalZone zone : zones) {
            Object lock = lockManager.getZoneLock(zone.getZoneId());

            synchronized (lock) {
                boolean wasIn = prevPos != null && zone.contains(prevPos);
                boolean isIn = zone.contains(currPos);

                if (wasIn && !isIn) {
                    lockManager.release(siteId, zone.getZoneId(), robotId);
                    logger.info("[{}] Released zone [{}] after exit", robotId, zone.getZoneId());
                }
            }
        }
    }
}
