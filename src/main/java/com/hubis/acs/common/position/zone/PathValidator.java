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
            System.out.println("Checking pos: " + pos);
            for (GlobalZone zone : zones) {
                boolean inZone = zone.contains(pos);
                System.out.println("Zone " + zone.getZoneId() + " contains pos " + pos + " ? " + inZone);
                if(inZone) {
                    String lockOwner = lockManager.getLockOwner(siteId, zone.getZoneId());
                    if (inZone && lockOwner != null && !lockOwner.equals(robotId)) {
                        System.out.println("Blocked by zone: " + zone.getZoneId() + ", owner: " + lockOwner + ", current robot: " + robotId);
                        return true;

                    }
                }
            }
        }
        return false;
    }

}