package com.hubis.acs.common.position.model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
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

    public boolean contains(List<Position> surroundingPoints) {
        // AABB 필터: 빠른 예외 처리
        double sMinX = surroundingPoints.stream().mapToDouble(Position::getX).min().orElse(Double.MAX_VALUE);
        double sMaxX = surroundingPoints.stream().mapToDouble(Position::getX).max().orElse(Double.MIN_VALUE);
        double sMinY = surroundingPoints.stream().mapToDouble(Position::getY).min().orElse(Double.MAX_VALUE);
        double sMaxY = surroundingPoints.stream().mapToDouble(Position::getY).max().orElse(Double.MIN_VALUE);

        if (this.maxX < sMinX || this.minX > sMaxX || this.maxY < sMinY || this.minY > sMaxY) {
            return false;
        }

        // 1. 포함 여부
        for (Position pos : surroundingPoints) {
            if (pointInsidePolygon(pos)) {
                return true;
            }
        }

        // 2. 겹침 / 접촉 여부 (edge 간 비교)
        List<Line> zoneEdges = getEdgesFromPoints(this.pointList);
        List<Line> surroundingEdges = getEdgesFromPoints(surroundingPoints);

        for (Line e1 : zoneEdges) {
            for (Line e2 : surroundingEdges) {
                if (linesIntersect(e1, e2) || linesTouch(e1, e2)) {
                    return true;
                }
            }
        }

        return false;
    }

    // 기존 단일 점 포함 확인 (Ray Casting)
    private boolean pointInsidePolygon(Position pos) {
        double x = pos.getX();
        double y = pos.getY();

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

    // Line 추출 (꼭짓점 리스트 → 선분 리스트)

    private List<Line> getEdgesFromPoints(List<? extends Position> points) {
        List<Line> edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Position a = points.get(i);
            Position b = points.get((i + 1) % points.size());
            edges.add(new Line(a, b));
        }
        return edges;
    }



    // 선분 교차 여부
    private boolean linesIntersect(Line l1, Line l2) {
        return ccw(l1.start, l1.end, l2.start) != ccw(l1.start, l1.end, l2.end)
                && ccw(l2.start, l2.end, l1.start) != ccw(l2.start, l2.end, l1.end);
    }

    // 선분 접촉 여부 (끝점 공유)
    private boolean linesTouch(Line l1, Line l2) {
        return l1.start.equals(l2.start) || l1.start.equals(l2.end) ||
                l1.end.equals(l2.start) || l1.end.equals(l2.end);
    }

    // CCW 판정 (Counter Clockwise)
    private boolean ccw(Position a, Position b, Position c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) -
                (b.getY() - a.getY()) * (c.getX() - a.getX()) > 0;
    }

    public boolean isBlocked() {
        return blocked;
    }

    // 내부 클래스: 선분 구조
    private static class Line {
        Position start;
        Position end;

        public Line(Position start, Position end) {
            this.start = start;
            this.end = end;
        }
    }
}
