package com.hubis.acs.common.cache;

import com.hubis.acs.repository.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AgentPositionCache {
    private static final AgentPositionCache instance = new AgentPositionCache();
    private final Map<String, Node> agentPositions = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private AgentPositionCache() {}

    public static AgentPositionCache getInstance() {
        return instance;
    }

    public void updatePosition(String agentName, Node position) {
        lock.writeLock().lock();
        try {
            agentPositions.put(agentName, position);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, Node> getAllPositions() {
        lock.readLock().lock();
        try {
            return new HashMap<>(agentPositions);
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isPathClear(List<Node> plannedPath, String agentName) {
        lock.readLock().lock();
        try {
            // 계획된 경로의 각 지점에 대해 다른 에이전트와의 충돌 검사
            for (Node pathNode : plannedPath) {
                for (Map.Entry<String, Node> entry : agentPositions.entrySet()) {
                    if (entry.getKey().equals(agentName)) continue;
                    
                    Node otherPosition = entry.getValue();
                    if (isCollision(pathNode, otherPosition)) {
                        return false;
                    }
                }
            }
            return true;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean isCollision(Node node1, Node node2) {
        return Math.abs(node1.x - node2.x) <= 2 && Math.abs(node1.y - node2.y) <= 2;
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            agentPositions.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
} 