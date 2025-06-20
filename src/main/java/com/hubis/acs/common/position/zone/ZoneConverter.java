package com.hubis.acs.common.position.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubis.acs.common.entity.ZoneMaster;
import com.hubis.acs.common.position.model.GlobalZone;
import com.hubis.acs.common.position.model.Point;

import java.util.List;

public class ZoneConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static GlobalZone fromEntity(ZoneMaster entity) {
        try {
            if (!"block".equalsIgnoreCase(entity.getZone_tp())) {
                return null; // Only 'block' zones are managed in memory
            }

            List<Point> points = mapper.readValue(entity.getPoint_val(), new TypeReference<>() {});
            if (points == null || points.size() < 2) {
                throw new IllegalArgumentException("Zone must have at least 2 points");
            }

            double minX = points.stream().mapToDouble(Point::getX).min().orElse(0);
            double maxX = points.stream().mapToDouble(Point::getX).max().orElse(0);
            double minY = points.stream().mapToDouble(Point::getY).min().orElse(0);
            double maxY = points.stream().mapToDouble(Point::getY).max().orElse(0);

            GlobalZone zone = new GlobalZone(entity.getZone_id(), minX, maxX, minY, maxY, points);
            return zone;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse point_val for Zone ID: " + entity.getZone_id(), e);
        }
    }

    public static ZoneMaster toEntity(GlobalZone zone, List<Point> points, Long mapUuid, String siteCd) {
        try {
            String pointVal = mapper.writeValueAsString(points);

            ZoneMaster entity = new ZoneMaster();
            entity.setZone_id(zone.getZoneId());
            entity.setZone_nm(zone.getZoneId()); // 필요시 이름 분리
            entity.setZone_tp("block");
            entity.setPoint_cnt(String.valueOf(points.size()));
            entity.setPoint_val(pointVal);
            entity.setMap_uuid(mapUuid);
            entity.setSite_cd(siteCd);
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert GlobalZone to ZoneMaster entity", e);
        }
    }
}
