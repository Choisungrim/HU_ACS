package com.hubis.acs.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Node {
    public static final double NODE_DISTANCE_MM = 1000.0; // 노드 간 거리 1000mm
    public static final double DEFAULT_SPEED_MPS = 0.5;   // 기본 이동 속도 0.5m/s
    
    public String NodeName;
    public int x, y, g, h, f;
    public Node parent;
    public List<Node> newPath;
    public int agentNumber;
    private double speed;  // m/s 단위

    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.agentNumber = -1;
        this.newPath = null;
        this.speed = DEFAULT_SPEED_MPS;  // 기본 속도 0.5m/s
        this.NodeName = "";
    }

    public Node(int x, int y, Node parent, int agentNumber, String NodeName) {
        this(x, y, parent);
        this.agentNumber = agentNumber;
        this.NodeName = NodeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return this.x == node.x && this.y == node.y; // x, y 좌표 비교
    }

    @Override
    public int hashCode() {
        return 31 * x + y; // hashCode 구현
    }

    // 현재 위치에서 다른 노드까지의 실제 거리(mm)를 계산하는 메서드 추가
    public double getDistanceTo(Node other) {
        double dx = Math.abs(this.x - other.x) * NODE_DISTANCE_MM;
        double dy = Math.abs(this.y - other.y) * NODE_DISTANCE_MM;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static List<Node> convertPointsToNodes(List<Point> points, Node parent) {
        List<Node> nodes = new ArrayList<>();

        for (Point point : points) {
            Node node = new Node(point.x, point.y, parent); // 기본 생성자 사용
            nodes.add(node);
        }

        return nodes;
    }

    public static List<Point> convertNodesToPoints(List<Node> points, Node parent) {
        List<Point> nodes = new ArrayList<>();

        for (Node point : points) {
            Point node = new Point(point.x, point.y); // 기본 생성자 사용
            nodes.add(node);
        }

        return nodes;
    }

    // 다른 노드까지 이동하는데 걸리는 예상 시간(초) 계산
    public double getTimeToNode(Node other) {
        return getDistanceTo(other) / (speed * 1000); // mm를 m로 변환하여 계산
    }
}