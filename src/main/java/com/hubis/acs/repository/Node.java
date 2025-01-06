package com.hubis.acs.repository;

import java.util.List;

public class Node {
    public static final double NODE_DISTANCE_MM = 1000.0; // 노드 간 거리 1000mm
    public static final double DEFAULT_SPEED_MPS = 0.5;   // 기본 이동 속도 0.5m/s
    
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
    }

    public Node(int x, int y, Node parent, int agentNumber) {
        this(x, y, parent);
        this.agentNumber = agentNumber;
    }

    public void setNewPath(List<Node> newPath) {
        this.newPath = newPath;
    }

    public List<Node> getNewPath() {
        return newPath;
    }

    public boolean hasNewPath() {
        return newPath != null;
    }

    public void setAgentNumber(int agentNumber) {
        this.agentNumber = agentNumber;
    }

    public int getAgentNumber() {
        return agentNumber;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
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

    // 다른 노드까지 이동하는데 걸리는 예상 시간(초) 계산
    public double getTimeToNode(Node other) {
        return getDistanceTo(other) / (speed * 1000); // mm를 m로 변환하여 계산
    }
}