package com.hubis.acs.common.position.handler;

import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Position;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GlobalZoneManager {

    // map_uuid → zone_id → GlobalZone
    private final Map<Long, Map<String, GlobalZone>> zoneMapByMap = new ConcurrentHashMap<>();

    /** 전체 맵 정보에 대한 등록 */
    public void registerZones(Long mapUuid, Collection<GlobalZone> zones) {
        Map<String, GlobalZone> zoneMap = new ConcurrentHashMap<>();
        for (GlobalZone zone : zones) {
            zoneMap.put(zone.getZoneId(), zone);
        }
        zoneMapByMap.put(mapUuid, zoneMap);
    }

    /** 개별 Zone 등록 */
    public void registerZone(Long mapUuid, GlobalZone zone) {
        zoneMapByMap.computeIfAbsent(mapUuid, k -> new ConcurrentHashMap<>()).put(zone.getZoneId(), zone);
    }

    /** 특정 위치가 차단된 Zone에 포함되는지 확인 */
    public boolean isPositionBlocked(Position pos, Long mapUuid) {
        Map<String, GlobalZone> zones = zoneMapByMap.get(mapUuid);
        if (zones == null) return false;

        for (GlobalZone zone : zones.values()) {
            if (zone.contains(pos) && zone.isBlocked()) {
                return true;
            }
        }
        return false;
    }

    /** 특정 zoneId에 대해 차단 여부 갱신 */
    public void setZoneBlocked(Long mapUuid, String zoneId, boolean blocked) {
        Map<String, GlobalZone> zones = zoneMapByMap.get(mapUuid);
        if (zones != null && zones.containsKey(zoneId)) {
            zones.get(zoneId).setBlocked(blocked);
        }
    }

    /** 특정 zoneId 조회 */
    public GlobalZone getZone(Long mapUuid, String zoneId) {
        Map<String, GlobalZone> zones = zoneMapByMap.get(mapUuid);
        return (zones != null) ? zones.get(zoneId) : null;
    }

    /** 특정 맵에 포함된 모든 Zone 반환 */
    public Collection<GlobalZone> getZonesByMap(Long mapUuid) {
        return zoneMapByMap.getOrDefault(mapUuid, Collections.emptyMap()).values();
    }

    /** 전체 맵 UUID 반환 */
    public Set<Long> getAllMapUuids() {
        return zoneMapByMap.keySet();
    }

    /** 모든 Zone 일괄 반환 */
    public Collection<GlobalZone> getAllZones() {
        List<GlobalZone> result = new ArrayList<>();
        for (Map<String, GlobalZone> zones : zoneMapByMap.values()) {
            result.addAll(zones.values());
        }
        return result;
    }
}
