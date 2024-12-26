package com.hubis.acs.repository;

import java.util.List;

public class Node {
    public int x, y, g, h, f;
    public Node parent;
    public List<Node> newPath;
    public int agentNumber;

    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.agentNumber = -1;
        this.newPath = null;
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
}