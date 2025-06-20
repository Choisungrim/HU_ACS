package com.hubis.acs.common.position.model;

import com.hubis.acs.common.position.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GlobalZone {
    private final String zoneId;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final List<Point> pointList; // 다각형 정보
    private boolean blocked;

    public GlobalZone(String zoneId, double minX, double maxX, double minY, double maxY, List<Point> pointList) {
        this.zoneId = zoneId;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.pointList = pointList;
        this.blocked = false;
    }

    public boolean contains(Position pos) {
        double x = pos.getX();
        double y = pos.getY();

        //AABB
        if (x < minX || x > maxX || y < minY || y > maxY) {
            return false;
        }

        //Ray Casting 다각형
        int crossingNumber = 0;
        int count = pointList.size();
        for (int i = 0; i < count; i++) {
            Point p1 = pointList.get(i);
            Point p2 = pointList.get((i + 1) % count);

            if (((p1.getY() > y) != (p2.getY() > y)) &&
                    (x < (p2.getX() - p1.getX()) * (y - p1.getY()) / (p2.getY() - p1.getY()) + p1.getX())) {
                crossingNumber++;
            }
        }
        return (crossingNumber % 2 == 1);
    }

    public boolean isBlocked() {
        return blocked;
    }
}
