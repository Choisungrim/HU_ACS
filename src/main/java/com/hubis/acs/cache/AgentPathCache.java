package com.hubis.acs.cache;

import com.hubis.acs.repository.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AgentPathCache {

    private static final Logger logger = LoggerFactory.getLogger(AgentPathCache.class);

    private static final AgentPathCache instance = new AgentPathCache();
    private final Map<String, CacheEntry> agentLocalPaths = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final long CACHE_EXPIRY_TIME = 60000; // 60초

    private static class CacheEntry {
        List<Node> path;
        long timestamp;
        
        CacheEntry(List<Node> path) {
            this.path = path;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_TIME;
        }
    }

    private AgentPathCache() {}

    public static AgentPathCache getInstance() {
        return instance;
    }

    public void updateLocalPath(String agentName, List<Node> localPath) {
        if (agentName == null || localPath == null) return;

        lock.writeLock().lock();
        try {
            if (!localPath.isEmpty()) {
                agentLocalPaths.put(agentName, new CacheEntry(new ArrayList<>(localPath)));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Node> getLocalPath(String agentName) {
        if (agentName == null) return new ArrayList<>();

        lock.readLock().lock();
        try {
            CacheEntry entry = agentLocalPaths.get(agentName);
            if (entry != null && !entry.isExpired()) {
                return new ArrayList<>(entry.path);
            }
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<String, List<Node>> getAllLocalPaths() {
        lock.readLock().lock();
        try {
            Map<String, List<Node>> paths = new HashMap<>();
            for (Map.Entry<String, CacheEntry> entry : agentLocalPaths.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    paths.put(entry.getKey(), new ArrayList<>(entry.getValue().path));
                }
            }
            return paths;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Node getCurrentPosition(String agentName) {
        if (agentName == null) return null;

        lock.readLock().lock();
        try {
            CacheEntry entry = agentLocalPaths.get(agentName);
            if (entry != null && !entry.isExpired()) {
                List<Node> path = entry.path;
                return path != null && !path.isEmpty() ? path.get(0) : null;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isPathConflict(String agentName, List<Node> plannedPath) {
        if (agentName == null || plannedPath == null || plannedPath.isEmpty()) {
            return false;
        }

        lock.readLock().lock();
        try {
            for (Map.Entry<String, CacheEntry> entry : agentLocalPaths.entrySet()) {
                if (entry.getKey() == null || entry.getKey().equals(agentName)) continue;

                List<Node> otherPath = entry.getValue().path;
                if (otherPath == null || otherPath.isEmpty()) continue;

                // 다른 에이전트의 현재 위치와 계획된 경로 간의 충돌 검사
                Node otherCurrentPos = otherPath.get(0);
                if (otherCurrentPos != null) {
                    for (Node pathNode : plannedPath) {
                        if (pathNode != null && isCollision(pathNode, otherCurrentPos)) {
                            return true;
                        }
                    }
                }

                // 다른 에이전트의 예상 경로와의 충돌 검사
                int checkLength = Math.min(otherPath.size(), plannedPath.size());
                for (int i = 1; i < checkLength; i++) {
                    Node otherNode = otherPath.get(i);
                    Node plannedNode = plannedPath.get(i);
                    if (otherNode != null && plannedNode != null &&
                            isCollision(plannedNode, otherNode)) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean isCollision(Node node1, Node node2) {
        if (node1 == null || node2 == null) return false;
        return Math.abs(node1.x - node2.x) <= 2 && Math.abs(node1.y - node2.y) <= 2;
    }

    public void moveToNextPosition(String agentName) {
        if (agentName == null) return;

        lock.writeLock().lock();
        try {
            CacheEntry entry = agentLocalPaths.get(agentName);
            if (entry != null && entry.path != null && entry.path.size() > 1) {
                entry.path.remove(0);
                agentLocalPaths.put(agentName, new CacheEntry(new ArrayList<>(entry.path)));
            }
        } catch (Exception e) {
            // 예외 발생 시 로그 기록
            logger.error("Error moving to next position for agent " + agentName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            agentLocalPaths.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}