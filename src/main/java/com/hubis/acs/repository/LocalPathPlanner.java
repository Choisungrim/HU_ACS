package com.hubis.acs.repository;
import com.hubis.acs.cache.AgentPathCache;
import com.hubis.acs.cache.AgentPositionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LocalPathPlanner {
    private static final Logger logger = LoggerFactory.getLogger(LocalPathPlanner.class);
    private boolean[][] grid; // 장애물 맵
    private GlobalPathFinder globalPathFinder;  // GlobalPathFinder 추가
    private Set<String> visitedPositions;  // 방문 기록 저장
    private static final int MAX_WAIT_COUNT = 3;  // 최대 대기 횟수
    private Map<String, Integer> waitCount = new HashMap<>();  // 에이전트별 대기 횟수
    private final Map<String, List<Node>> localPathCache = new ConcurrentHashMap<>();
    private static final long CACHE_TIMEOUT = 5000; // 5초 캐시 유효시간
    private final AgentPositionCache positionCache = AgentPositionCache.getInstance();
    private final AgentPathCache pathCache = AgentPathCache.getInstance();

    public LocalPathPlanner(boolean[][] grid) {
        this.grid = grid;
        this.globalPathFinder = new GlobalPathFinder();  // 초기화
        this.visitedPositions = new HashSet<>();  // 방문 기록 초기화
    }

    public List<Node> planLocalPath(List<Node> globalPath, List<Node> otherAgentPaths, String agentName, 
                                  Node currentPosition, Node destination) {
        List<Node> localPath = new ArrayList<>();
        localPath.add(currentPosition);

        if (!currentPosition.equals(destination) && globalPath.size() > 1) {
            // 현재 대기 횟수 확인
            int currentWaitCount = waitCount.getOrDefault(agentName, 0);
            Node nextPosition = globalPath.get(1);

            // 대기 횟수가 임계값을 초과한 경우 우회 경로 시도
            if (currentWaitCount >= MAX_WAIT_COUNT) {
                logger.info(agentName + " trying to find detour path after waiting " + currentWaitCount + " times");
                
                // 우회 경로 시도 (상하좌우만 사용)
                List<Node> detourPath = findDetourPath(currentPosition, destination, agentName);
                if (!detourPath.isEmpty()) {
                    waitCount.put(agentName, 0);  // 대기 횟수 초기화
                    pathCache.updateLocalPath(agentName, detourPath);
                    logger.info(agentName + " found detour path");
                    return detourPath;
                }

                // 우회 실패 시 전역 경로 재계산
                List<Node> replanPath = globalPathFinder.findGlobalPath(
                    currentPosition.x, currentPosition.y,
                    destination.x, destination.y,
                    grid
                );
                if (!replanPath.isEmpty()) {
                    waitCount.put(agentName, 0);
                    pathCache.updateLocalPath(agentName, replanPath);
                    logger.info(agentName + " replanned global path");
                    return replanPath;
                }
            }

            // 일반적인 이동 시도
            boolean hasDirectCollision = false;
            for (Node other : otherAgentPaths) {
                if (other.equals(nextPosition) || 
                    (Math.abs(other.x - nextPosition.x) <= 1 && 
                     Math.abs(other.y - nextPosition.y) <= 1)) {
                    hasDirectCollision = true;
                    break;
                }
            }

            if (!hasDirectCollision) {
                waitCount.put(agentName, 0);
                localPath.add(nextPosition);
                pathCache.updateLocalPath(agentName, localPath);
                return localPath;
            }

            // 대기 횟수 증�� 및 로깅
            waitCount.put(agentName, currentWaitCount + 1);
            logger.info(agentName + " waiting due to collision at " + nextPosition + 
                       " (wait count: " + currentWaitCount + "/" + MAX_WAIT_COUNT + ")");
        }

        pathCache.updateLocalPath(agentName, localPath);
        return localPath;
    }

    private List<Node> findDetourPath(Node start, Node end, String agentName) {
        // 상하좌우 방향만 사용
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        List<DirectionScore> possiblePaths = new ArrayList<>();
        
        // 목적지 방향
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        
        for (int[] dir : directions) {
            int newX = start.x + dir[0];
            int newY = start.y + dir[1];
            
            if (!isValidPosition(newX, newY)) continue;
            
            String posKey = newX + "," + newY;
            if (visitedPositions.contains(posKey)) continue;

            Node nextNode = new Node(newX, newY, start);
            List<Node> plannedPath = new ArrayList<>();
            plannedPath.add(nextNode);
            
            if (positionCache.isPathClear(plannedPath, agentName)) {
                // 목적지까지의 경로 찾기
                List<Node> pathToEnd = globalPathFinder.findGlobalPath(newX, newY, end.x, end.y, grid);
                if (!pathToEnd.isEmpty()) {
                    List<Node> completePath = new ArrayList<>();
                    completePath.add(start);
                    completePath.addAll(pathToEnd);
                    
                    // 점수 계산 (목적지 방향 우선)
                    int score = calculateDetourScore(dx, dy, dir[0], dir[1]);
                    possiblePaths.add(new DirectionScore(nextNode, score, completePath));
                }
            }
        }
        
        if (!possiblePaths.isEmpty()) {
            possiblePaths.sort((a, b) -> b.score - a.score);
            DirectionScore bestPath = possiblePaths.get(0);
            visitedPositions.add(bestPath.node.x + "," + bestPath.node.y);
            return bestPath.fullPath;
        }
        
        return new ArrayList<>();
    }

    private int calculateDetourScore(int targetDx, int targetDy, int moveDx, int moveDy) {
        int score = 100;  // 기본 점수
        
        // 목적지 방향과 같은 방향으로 이동하면 높은 점수
        if (Math.signum(targetDx) == Math.signum(moveDx)) score += 50;
        if (Math.signum(targetDy) == Math.signum(moveDy)) score += 50;
        
        // 목적지 반대 방향으로 이동하면 낮은 점수
        if (Math.signum(targetDx) == -Math.signum(moveDx)) score -= 30;
        if (Math.signum(targetDy) == -Math.signum(moveDy)) score -= 30;
        
        return score;
    }

    private int calculatePathScore(List<Node> path, Node destination) {
        int score = 1000;
        
        // 경로 길이에 따른 페널티 (짧을수록 좋음)
        score -= path.size() * 10;
        
        // 목적지까지의 직선 거리와의 차이에 따른 페널티
        Node lastNode = path.get(path.size() - 1);
        if (!lastNode.equals(destination)) {
            int directDistance = Math.abs(destination.x - lastNode.x) + Math.abs(destination.y - lastNode.y);
            score -= directDistance * 30;  // 목적지 도달 실패에 대한 큰 페널티
        }
        
        // 방향 전환 횟수에 따른 페널티
        int directionChanges = countDirectionChanges(path);
        score -= directionChanges * 15;
        
        return score;
    }

    private int countDirectionChanges(List<Node> path) {
        if (path.size() < 3) return 0;
        
        int changes = 0;
        for (int i = 2; i < path.size(); i++) {
            Node prev = path.get(i-2);
            Node curr = path.get(i-1);
            Node next = path.get(i);
            
            int dx1 = curr.x - prev.x;
            int dy1 = curr.y - prev.y;
            int dx2 = next.x - curr.x;
            int dy2 = next.y - curr.y;
            
            if (dx1 != dx2 || dy1 != dy2) {
                changes++;
            }
        }
        return changes;
    }

    // 기본적인 위치 유효성 검사
    private boolean isValidPosition(int x, int y) {
        // 계 체크
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length) {
            return false;
        }
        
        // 장애물 체크
        return !grid[x][y];
    }

    // 다른 에이전트들과의 충돌을 고려한 이동 가능 여부 체크
    private boolean isValidMove(int x, int y, List<Node> otherAgentPaths, Node current) {
        // 기본 위치 유효성 검사
        if (!isValidPosition(x, y)) {
            return false;
        }

        // 이전 방문 위치 체크
        String position = x + "," + y;
        if (visitedPositions.contains(position)) {
            return false;
        }
        
        // 다른 에이전트와의 충돌 체크
        Node newNode = new Node(x, y, current);
        return !detectCollision(newNode, otherAgentPaths, current);
    }

    private boolean detectAnyCollision(List<Node> otherAgentPaths, Node currentPosition) {
        // 주변 2칸 이내에 다른 에이전트가 있는지 빠르게 확인
        for (Node other : otherAgentPaths) {
            if (Math.abs(other.x - currentPosition.x) <= 2 && 
                Math.abs(other.y - currentPosition.y) <= 2) {
                return true;
            }
        }
        return false;
    }

    private List<Node> calculateLocalPath(List<Node> globalPath, List<Node> otherAgentPaths, String agentName, 
                                        Node currentPosition, Node destination) {
        List<Node> localPath = new ArrayList<>();
        localPath.add(currentPosition);

        if (!currentPosition.equals(destination) && globalPath.size() > 1) {
            Node nextPosition = globalPath.get(1);
            
            if (detectCollision(nextPosition, otherAgentPaths, currentPosition)) {
                logger.info("Collision detected at " + nextPosition + " for " + agentName);
                
                // 대기 횟수 증가
                int currentWaitCount = waitCount.getOrDefault(agentName, 0) + 1;
                waitCount.put(agentName, currentWaitCount);

                // 우선순위 기반 대기 또는 이동 결정
                int agentNumber = Integer.parseInt(agentName.substring(5));
                boolean shouldWait = shouldAgentWait(agentNumber, otherAgentPaths);
                
                // 대기 횟수가 임계값을 초과하거나 우선순위가 높은 경우 우회 시도
                if (currentWaitCount > MAX_WAIT_COUNT || !shouldWait) {
                    // 우회 경로 탐색
                    List<Node> detourPath = findDetourPath(currentPosition, destination, agentName);
                    if (!detourPath.isEmpty() && detourPath.size() > 1) {
                        waitCount.put(agentName, 0);  // 대기 횟수 초기화
                        Node nextNode = detourPath.get(1);
                        nextNode.setNewPath(detourPath);  // 전체 우회 경로 설정
                        localPath.add(nextNode);  // 다음 이동 위치 추가
                        logger.info(agentName + " taking detour path through " + nextNode);
                        return localPath;
                    }
                }

                // 우회 실패 시 대기
                if (shouldWait || currentWaitCount <= MAX_WAIT_COUNT) {
                    logger.info(agentName + " waiting at current position (wait count: " + currentWaitCount + ")");
                    return localPath;  // 현재 위치만 포함
                }

                // 모든 시도가 실패한 경우 강제로 새로운 경로 계산
                List<Node> forcedPath = findCompletelyNewPath(currentPosition, destination, otherAgentPaths);
                if (!forcedPath.isEmpty() && forcedPath.size() > 1) {
                    Node nextNode = forcedPath.get(1);
                    nextNode.setNewPath(forcedPath);
                    localPath.add(nextNode);
                    logger.info(agentName + " forced to take new path through " + nextNode);
                    return localPath;
                }
            } else {
                // 충돌이 없는 경우 원래 경로 따라가기
                waitCount.put(agentName, 0);  // 대기 횟수 초기화
                localPath.add(nextPosition);
                logger.info(agentName + " moving to original path: " + nextPosition);
            }
        }

        return localPath;
    }

    private boolean detectCollision(Node targetNode, List<Node> otherAgentPaths, Node currentPosition) {
        for (Node otherNode : otherAgentPaths) {
            // 직접적인 충돌만 체크
            if (otherNode.equals(targetNode)) {
                return true;
            }
            
            // 인접한 위치에 있는 경우만 충돌로 판단
            if (Math.abs(otherNode.x - targetNode.x) <= 1 && 
                Math.abs(otherNode.y - targetNode.y) <= 1) {
                return true;
            }
        }
        return false;
    }

    private Node findAlternativeNode(Node current, List<Node> otherAgentPaths, Node destination) {
        int[][] directions = {
            {-1, 0},  // 상
            {1, 0},   // 하
            {0, -1},  // 좌
            {0, 1}    // 우
        };
        
        // 목적지 방향 계산
        int targetDx = destination.x - current.x;
        int targetDy = destination.y - current.y;
        
        // 각 방향에 대한 점수 계산 (목적지에 가까워지는 방향 선호)
        List<DirectionScore> directionScores = new ArrayList<>();
        
        // 1단계: 직접 이동 가능한 노드 탐색
        for (int[] direction : directions) {
            int newX = current.x + direction[0];
            int newY = current.y + direction[1];

            if (isValidMove(newX, newY, otherAgentPaths, current)) {
                Node newNode = new Node(newX, newY, current);
                int score = calculateScore(newNode, destination, current);
                directionScores.add(new DirectionScore(newNode, score));
            }
        }

        // 2단계: 2칸 이동 탐색 (더 멀리 우회)
        if (directionScores.isEmpty()) {
            for (int[] dir1 : directions) {
                int newX = current.x + dir1[0] * 2;
                int newY = current.y + dir1[1] * 2;

                if (isValidMove(newX, newY, otherAgentPaths, current)) {
                    Node newNode = new Node(newX, newY, current);
                    int score = calculateScore(newNode, destination, current);
                    directionScores.add(new DirectionScore(newNode, score));
                }
            }
        }
        
        // 점수에 따라 정하고 적의 방향 선택
        if (!directionScores.isEmpty()) {
            directionScores.sort((a, b) -> b.score - a.score);
            return directionScores.get(0).node;
        }
        
        return null;
    }

    private int calculateScore(Node node, Node destination, Node current) {
        // Manhattan distance to destination
        int distToDestination = Math.abs(destination.x - node.x) + Math.abs(destination.y - node.y);
        int currentDistToDestination = Math.abs(destination.x - current.x) + Math.abs(destination.y - current.y);
        
        // 목적지에 가까워지는 정도에 따른 점수
        int progressScore = currentDistToDestination - distToDestination;
        
        // 현재 위치에서 멀어지는 정도에 따른 페널티
        int distFromCurrent = Math.abs(node.x - current.x) + Math.abs(node.y - current.y);
        
        return progressScore * 2 - distFromCurrent;
    }

    private boolean shouldAgentWait(int currentAgentNumber, List<Node> otherAgentPaths) {
        // 현재 에이전트의 번호와 충돌 지점의 다른 에이전트들의 번호를 비교
        for (Node otherAgent : otherAgentPaths) {
            if (otherAgent.agentNumber < currentAgentNumber) {
                return true;  // 더 낮은 번호의 에이전트가 있으면 대기
            }
        }
        return false;
    }

    private List<Node> findCompletelyNewPath(Node start, Node end, List<Node> otherAgentPaths) {
        // 현재 방향과 완전히 다른 방향으로의 우회 시도
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        
        // 8방향으로 확장된 탐색 (더 멀리 우회)
        int[][] directions = {
            {-2, -2}, {-2, 0}, {-2, 2},
            {0, -2}, {0, 2},
            {2, -2}, {2, 0}, {2, 2}
        };
        
        for (int[] dir : directions) {
            int newX = start.x + dir[0];
            int newY = start.y + dir[1];
            
            if (!isValidPosition(newX, newY)) continue;
            
            String posKey = newX + "," + newY;
            if (visitedPositions.contains(posKey)) continue;
            
            boolean hasCollision = false;
            for (Node other : otherAgentPaths) {
                if (Math.abs(other.x - newX) <= 1 && Math.abs(other.y - newY) <= 1) {
                    hasCollision = true;
                    break;
                }
            }
            
            if (!hasCollision) {
                List<Node> pathToEnd = globalPathFinder.findGlobalPath(newX, newY, end.x, end.y, grid);
                if (!pathToEnd.isEmpty()) {
                    List<Node> completePath = new ArrayList<>();
                    completePath.add(start);
                    completePath.addAll(pathToEnd);
                    visitedPositions.add(posKey);
                    return completePath;
                }
            }
        }
        
        return new ArrayList<>();
    }

    private List<Node> findAggressiveDetourPath(Node start, Node end, String agentName, List<Node> otherAgentPaths) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};  // 상하좌우만 사용
        List<DirectionScore> possiblePaths = new ArrayList<>();
        
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        
        for (int[] dir : directions) {
            int newX = start.x + dir[0];
            int newY = start.y + dir[1];
            
            if (!isValidPosition(newX, newY)) continue;
            
            String posKey = newX + "," + newY;
            if (visitedPositions.contains(posKey)) continue;

            boolean hasCollision = false;
            for (Node other : otherAgentPaths) {
                if (Math.abs(other.x - newX) <= 1 && Math.abs(other.y - newY) <= 1) {
                    hasCollision = true;
                    break;
                }
            }
            
            if (!hasCollision) {
                Node nextNode = new Node(newX, newY, start);
                List<Node> path = new ArrayList<>();
                path.add(start);
                path.add(nextNode);
                
                int score = calculateDetourScore(dx, dy, dir[0], dir[1]);
                possiblePaths.add(new DirectionScore(nextNode, score, path));
            }
        }
        
        if (!possiblePaths.isEmpty()) {
            possiblePaths.sort((a, b) -> b.score - a.score);
            DirectionScore bestPath = possiblePaths.get(0);
            visitedPositions.add(bestPath.node.x + "," + bestPath.node.y);
            return bestPath.fullPath;
        }
        
        return new ArrayList<>();
    }

    // DirectionScore 내부 클래스 수정
    private static class DirectionScore {
        Node node;
        int score;
        List<Node> fullPath;  // 전체 경로 저장

        
        // 기본 생성자 (단순 방향 점수용)
        DirectionScore(Node node, int score) {
            this.node = node;
            this.score = score;
            this.fullPath = new ArrayList<>();
        }

        // 전체 경로 포함 생성자 (우회 경로용)
        DirectionScore(Node node, int score, List<Node> fullPath) {
            this.node = node;
            this.score = score;
            this.fullPath = fullPath;
        }
    }
}
