package com.hubis.acs.common.utils;

import com.hubis.acs.common.position.model.Point;
import com.hubis.acs.common.position.model.Position;

public class PositionUtils {

    public static Point toPoint(Position pos) {
        return new Point(pos.getX(), pos.getY());
    }

    public static Position toPosition(Point point, double theta) {
        return new Position(point.getX(), point.getY(), theta);
    }
}
