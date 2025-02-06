package com.hubis.acs.common.configuration;

import com.hubis.acs.common.cache.AgentPathCache;
import com.hubis.acs.common.cache.AgentPositionCache;
import com.hubis.acs.repository.LocalPathPlanner;
import com.hubis.acs.repository.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;

public class AgentRunnable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AgentRunnable.class);
    private static final int MOVE_INTERVAL = 1500; // 2초 간격으로 이동으로 변경

    private List<Node> globalPath;
    private String agentName;
    private Node currentPosition;
    private Node destination;
    private LocalPathPlanner localPathPlanner;
    private List<Node> otherAgentPaths;
    private List<List<Node>> agentLocalPaths;
    private List<Node> sharedCurrentPositions;
    private int agentIndex;
    private int currentPathIndex = 0;
    private List<List<Node>> sharedGlobalPaths;

    public AgentRunnable(List<Node> globalPath, String agentName, Node startPosition, 
                        Node destination, LocalPathPlanner localPathPlanner, 
                        List<Node> otherAgentPaths, List<List<Node>> agentLocalPaths,
                        List<Node> sharedCurrentPositions, int agentIndex,
                        List<List<Node>> sharedGlobalPaths) {
        this.globalPath = globalPath;
        this.agentName = agentName;
        this.currentPosition = startPosition;
        this.destination = destination;
        this.localPathPlanner = localPathPlanner;
        this.otherAgentPaths = otherAgentPaths;
        this.agentLocalPaths = agentLocalPaths;
        this.sharedCurrentPositions = sharedCurrentPositions;
        this.agentIndex = agentIndex;
        this.sharedGlobalPaths = sharedGlobalPaths;
    }

    @Override
    public void run() {
        AgentPositionCache positionCache = AgentPositionCache.getInstance();
        AgentPathCache pathCache = AgentPathCache.getInstance();
        positionCache.updatePosition(agentName, currentPosition);

        while (!currentPosition.equals(destination)) {
            long startTime = System.currentTimeMillis();

            // 현재 위치부터의 경로만 전달
            List<Node> remainingPath = new ArrayList<>();
            boolean foundCurrent = false;
            for (Node node : globalPath) {
                if (node.equals(currentPosition)) {
                    foundCurrent = true;
                }
                if (foundCurrent) {
                    remainingPath.add(node);
                }
            }

            // 로컬 경로 계산
            List<Node> localPath = localPathPlanner.planLocalPath(
                remainingPath, otherAgentPaths, agentName, 
                currentPosition, destination
            );

            if (localPath.isEmpty()) {
                logger.warn(agentName + " has no valid path to follow.");
                try {
                    Thread.sleep(MOVE_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                continue;
            }

            // 새로운 경로가 생성된 경우 전역 경로 업데이트
            if (localPath.size() > 1 && !localPath.get(1).equals(remainingPath.get(1))) {
                globalPath = new ArrayList<>(localPath);
                
                // 공유 경로 업데이트
                synchronized(sharedGlobalPaths) {
                    sharedGlobalPaths.set(agentIndex, new ArrayList<>(globalPath));
                }
                
                // 다른 에이전트들의 현재 위치 정보 즉시 업데이트
                synchronized(sharedCurrentPositions) {
                    otherAgentPaths = new ArrayList<>(sharedCurrentPositions);
                    otherAgentPaths.remove(agentIndex);
                }
                
                logger.info(agentName + " found new path and updated global path");
                continue;
            }

            // 다음 위치로 이동
            if (localPath.size() > 1) {
                Node nextNode = localPath.get(1);
                
                // 실제 이동 전에 남은 시간 계산
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = Math.max(0, MOVE_INTERVAL - elapsedTime);
                
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                // 이동 실행
                currentPosition = nextNode;
                
                // 위치 캐시 업데이트
                positionCache.updatePosition(agentName, currentPosition);
                pathCache.moveToNextPosition(agentName);
                
                synchronized(sharedCurrentPositions) {
                    sharedCurrentPositions.set(agentIndex, currentPosition);
                }
                
                logger.info(agentName + " moved to " + currentPosition);
                
                // 이동 후 즉시 다른 에이전트들의 위치 업데이트
                synchronized(sharedCurrentPositions) {
                    otherAgentPaths = new ArrayList<>(sharedCurrentPositions);
                    otherAgentPaths.remove(agentIndex);
                }
            } else {
                // 이동할 수 없는 경우 대기
                try {
                    Thread.sleep(MOVE_INTERVAL);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                logger.info(agentName + " waiting at " + currentPosition);
            }
        }
        
        logger.info(agentName + " reached the destination: " + currentPosition);
    }
}