package com.hubis.acs.common.position.handler;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ZoneLockManager {

    // siteCd -> (zoneId -> robotId)
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> lockedZones = new ConcurrentHashMap<>();

    /** 점유 시도 */
    public boolean lock(String siteCd, String zoneId, String robotId) {
        lockedZones.putIfAbsent(siteCd, new ConcurrentHashMap<>());
        ConcurrentHashMap<String, String> siteLocks = lockedZones.get(siteCd);
        return siteLocks.putIfAbsent(zoneId, robotId) == null;
    }

    /** 점유 해제 */
    public void release(String siteCd, String zoneId, String robotId) {
        ConcurrentHashMap<String, String> siteLocks = lockedZones.get(siteCd);
        if (siteLocks != null && robotId.equals(siteLocks.get(zoneId))) {
            siteLocks.remove(zoneId);
        }
    }

    /** 특정 site의 점유 현황 조회 */
    public Map<String, String> getLocksBySite(String siteCd) {
        ConcurrentHashMap<String, String> map = lockedZones.get(siteCd);
        return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();

    }

    /** 전체 점유 현황 조회 */
    public Map<String, ConcurrentHashMap<String, String>> getAllLocks() {
        return lockedZones;
    }

    public boolean isLocked(String siteCd, String zoneId) {
        ConcurrentHashMap<String, String> siteLocks = lockedZones.get(siteCd);
        return siteLocks != null && siteLocks.containsKey(zoneId);
    }

    public String getLockOwner(String siteCd, String zoneId) {
        ConcurrentHashMap<String, String> siteLocks = lockedZones.get(siteCd);
        return siteLocks != null ? siteLocks.get(zoneId) : null;
    }

}
