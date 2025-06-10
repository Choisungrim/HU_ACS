package com.hubis.acs.common.position.model;

import lombok.Getter;

/**
 */
@Getter
public class Position {

    private final double x;      // mm 또는 m 단위의 X 좌표
    private final double y;      // mm 또는 m 단위의 Y 좌표
    private final double theta;  // 회전 방향 (degrees 또는 radians)

    public Position(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
        this.theta = 0;
    }

    public double distanceTo(Position other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return String.format("Position(x=%.2f, y=%.2f, θ=%.2f°)", x, y, theta);
    }
}
