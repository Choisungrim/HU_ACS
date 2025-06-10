package com.hubis.acs.common.position.model;

import com.hubis.acs.common.position.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalZone {
    private final String zoneId;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private boolean blocked;

    public GlobalZone(String zoneId, double minX, double maxX, double minY, double maxY) {
        this.zoneId = zoneId;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.blocked = false;
    }

    public boolean contains(Position pos) {
        double x = pos.getX();
        double y = pos.getY();
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
