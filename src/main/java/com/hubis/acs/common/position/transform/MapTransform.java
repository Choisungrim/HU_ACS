package com.hubis.acs.common.position.transform;

import com.hubis.acs.common.position.model.Point;

public class MapTransform {
    private final Point localOriginInGlobal;
    private final double rotationDeg;
    private final double resolution; // 예: 1.0 (mm), 0.02 (grid → m), 20.0 (grid → mm)

    public MapTransform(Point localOriginInGlobal, double rotationDeg, double resolution) {
        this.localOriginInGlobal = localOriginInGlobal;
        this.rotationDeg = rotationDeg;
        this.resolution = resolution;
    }

    // 로컬 좌표 → 글로벌 좌표
    public Point toGlobal(Point local) {
        // Step 1: 로컬 좌표 단위 보정 (그리드 단위 → mm)
        double scaledX = local.x * resolution;
        double scaledY = local.y * resolution;

        // Step 2: 회전 적용
        double theta = Math.toRadians(rotationDeg);
        double x = scaledX * Math.cos(theta) - scaledY * Math.sin(theta);
        double y = scaledX * Math.sin(theta) + scaledY * Math.cos(theta);

        // Step 3: 오프셋 적용
        return new Point(x + localOriginInGlobal.x, y + localOriginInGlobal.y);
    }

    // 글로벌 좌표 → 로컬 좌표
    public Point toLocal(Point global) {
        // Step 1: 오프셋 제거
        double dx = global.x - localOriginInGlobal.x;
        double dy = global.y - localOriginInGlobal.y;

        // Step 2: 회전 역변환
        double theta = Math.toRadians(-rotationDeg);
        double x = dx * Math.cos(theta) - dy * Math.sin(theta);
        double y = dx * Math.sin(theta) + dy * Math.cos(theta);

        // Step 3: 단위 역보정 (mm → 그리드)
        return new Point(x / resolution, y / resolution);
    }
}



