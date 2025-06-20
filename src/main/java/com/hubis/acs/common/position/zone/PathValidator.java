package com.hubis.acs.common.position.zone;

import com.hubis.acs.common.position.handler.GlobalZoneManager;
import com.hubis.acs.common.position.handler.ZoneLockManager;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Position;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PathValidator {
    private final GlobalZoneManager zoneManager;
    private final ZoneLockManager lockManager;

    public PathValidator(GlobalZoneManager zoneManager, ZoneLockManager lockManager) {
        this.zoneManager = zoneManager;
        this.lockManager = lockManager;
    }

    public boolean isPathBlocked(String siteId, long mapuuid, List<Position> path, String robotId) {
        Collection<GlobalZone> zones = zoneManager.getZonesByMap(mapuuid);

        for (Position pos : path) {
            for (GlobalZone zone : zones) {
                if (zone.contains(pos)) {
                    String lockOwner = lockManager.getLockOwner(siteId, zone.getZoneId());
                    if (lockOwner != null && !lockOwner.equals(robotId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}