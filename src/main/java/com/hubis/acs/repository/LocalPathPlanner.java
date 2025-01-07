package com.hubis.acs.repository;

import com.hubis.acs.service.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.PriorityQueue;
import java.util.Collections;

public class LocalPathPlanner {
    private static final Logger logger = LoggerFactory.getLogger(LocalPathPlanner.class);

    private static final int MAX_WAIT_COUNT = 1;    // 기본 대기 횟수
    private static final int STUCK_WAIT_TIME = 5000; // 5초 대기
    private static final int MAX_DETOUR_DISTANCE = 5;  // 최대 우회 거리
    private static final int MAX_WAIT_ATTEMPTS = 3;  // 최대 대기 시도 횟수

    private final Map<String, Integer> waitCount = new ConcurrentHashMap<>();
    private final Map<String, Integer> retryCount = new ConcurrentHashMap<>();

    private final boolean[][] grid;

    private final GlobalPathFinder globalPathFinder;
    private final Map<String, Set<String>> visitedPaths = new ConcurrentHashMap<>();
    private static final int PATH_MEMORY_LIMIT = 10;  // 최근 경로 기억 개수
    private final Map<String, Integer> stuckCount = new ConcurrentHashMap<>();      // 막힘 횟수 카운트
    private final Map<String, Long> stuckWaitStartTime = new ConcurrentHashMap<>(); // 대기 시작 시간 저장
    private final Map<String, Set<String>> cycleDetection = new ConcurrentHashMap<>(); // 순환 경로 감지용
    private final Map<String, List<Node>> previousPositions = new ConcurrentHashMap<>(); // 이전 위치 기록
    private final Map<String, Integer> waitAttempts = new ConcurrentHashMap<>();  // 대기 시도 횟수 추적

    public LocalPathPlanner(boolean[][] grid, AlgorithmService algorithmService) {
        this.grid = grid;
        this.globalPathFinder = new GlobalPathFinder(algorithmService);
    }

    public List<Node> planLocalPath(List<Node> globalPath, List<Node> otherAgentPaths, 
                                  String agentName, Node currentPosition, Node destination) {
        // 목적지 도달 확인 추가
        if (currentPosition.x == destination.x && currentPosition.y == destination.y) {
            logger.info(String.format("%s - Reached destination: (%d, %d)", 
                agentName, destination.x, destination.y));
            resetAgentState(agentName);  // 상태 초기화
            return Arrays.asList(currentPosition);
        }

        if (globalPath.size() < 2) return Arrays.asList(currentPosition);

        // 글로벌 패스 정보 로깅
        logger.info(String.format("%s - Global Path: %s", agentName, formatPath(globalPath)));

        // 대기 중인 경우 체크
        Long waitStartTime = stuckWaitStartTime.get(agentName);
        if (waitStartTime != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - waitStartTime < STUCK_WAIT_TIME) {
                logger.info(String.format("%s - Waiting at (%d, %d)", 
                    agentName, currentPosition.x, currentPosition.y));
                return Arrays.asList(currentPosition);
            } else {
                logger.info(String.format("%s - Replanning path after wait", agentName));
                int attempts = waitAttempts.getOrDefault(agentName, 0) + 1;
                waitAttempts.put(agentName, attempts);

                if (attempts >= MAX_WAIT_ATTEMPTS) {
                    logger.info(String.format("%s - Forcing move after max wait attempts", agentName));
                    List<Node> forcedPath = findSimpleDetour(currentPosition, destination);
                    if (!forcedPath.isEmpty()) {
                        resetAgentState(agentName);
                        return forcedPath;
                    }
                }
                resetAgentState(agentName);
            }
        }

        // 충돌 검사 및 경로 계획
        List<Node> nextSteps = getNextSteps(globalPath, currentPosition);
        if (!detectCollision(nextSteps, otherAgentPaths)) {
            // 충돌이 없을 때 로컬 패스 로깅
            List<Node> localPath = new ArrayList<>();
            localPath.add(currentPosition);
            localPath.add(globalPath.get(1));
            logger.info(String.format("%s - Local Path: %s", agentName, formatPath(localPath)));
            return localPath;
        }

        // 충돌이 있는 경우 우회 경로 탐색
        List<Node> detourPath = findSimpleDetour(currentPosition, destination);
        if (!detourPath.isEmpty()) {
            logger.info(String.format("%s - Detour path: %s", agentName, formatPath(detourPath)));
            return detourPath;
        }

        // 우회 실패시 대기
        int currentWaitCount = waitCount.getOrDefault(agentName, 0);
        if (currentWaitCount >= MAX_WAIT_COUNT) {
            stuckWaitStartTime.put(agentName, System.currentTimeMillis());
            logger.info(String.format("%s - Starting wait at (%d, %d)", 
                agentName, currentPosition.x, currentPosition.y));
            return Arrays.asList(currentPosition);
        }

        waitCount.put(agentName, currentWaitCount + 1);
        return Arrays.asList(currentPosition);
    }

    private List<Node> findSimpleDetour(Node current, Node destination) {
        // 상하좌우 방향으로만 이동
        int[][] directions = {
            {0, 1},   // 상
            {1, 0},   // 우
            {0, -1},  // 하
            {-1, 0}   // 좌
        };

        // 목적지 방향 우선순위 계산
        int dx = destination.x - current.x;
        int dy = destination.y - current.y;
        
        // 목적지 방향 우선
        List<int[]> prioritizedDirs = new ArrayList<>();
        for (int[] dir : directions) {
            if ((dir[0] == Integer.signum(dx) && dx != 0) || 
                (dir[1] == Integer.signum(dy) && dy != 0)) {
                prioritizedDirs.add(0, dir);  // 목적지 방향 우선
            } else {
                prioritizedDirs.add(dir);     // 다른 방향은 후순위
            }
        }

        // 목적지까지의 전체 경로 생성
        for (int[] dir : prioritizedDirs) {
            List<Node> completePath = new ArrayList<>();
            completePath.add(current);
            
            Node currentNode = current;
            Set<String> visited = new HashSet<>();  // 방문한 노드 추적
            visited.add(currentNode.x + "," + currentNode.y);

            while (currentNode.x != destination.x || currentNode.y != destination.y) {
                Node nextNode = null;
                double minDistance = Double.MAX_VALUE;

                // 가능한 모든 방향 시도
                for (int[] tryDir : directions) {
                    int newX = currentNode.x + tryDir[0];
                    int newY = currentNode.y + tryDir[1];
                    String pos = newX + "," + newY;

                    if (!isValidPosition(newX, newY) || visited.contains(pos)) continue;

                    // 목적지까지의 거리 계산
                    double distance = Math.sqrt(
                        Math.pow(destination.x - newX, 2) + 
                        Math.pow(destination.y - newY, 2)
                    );

                    // 더 가까운 경로 선택
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextNode = new Node(newX, newY, currentNode);
                    }
                }

                if (nextNode == null) break;  // 더 이상 진행할 수 없음

                completePath.add(nextNode);
                visited.add(nextNode.x + "," + nextNode.y);
                currentNode = nextNode;
            }

            // 목적지에 도달했는지 확인
            if (currentNode.x == destination.x && currentNode.y == destination.y) {
                return completePath;
            }
        }

        return new ArrayList<>();  // 경로를 찾지 못한 경우
    }

    private List<Node> getNextSteps(List<Node> globalPath, Node currentPosition) {
        List<Node> nextSteps = new ArrayList<>();
        nextSteps.add(currentPosition);  // 현재 위치도 포함
        
        int currentIndex = globalPath.indexOf(currentPosition);
        if (currentIndex >= 0 && currentIndex + 1 < globalPath.size()) {
            nextSteps.add(globalPath.get(currentIndex + 1));  // 다음 위치
        }
        
        return nextSteps;
    }

    private boolean detectCollision(List<Node> nextSteps, List<Node> otherAgentPaths) {
        for (Node step : nextSteps) {
            for (Node other : otherAgentPaths) {
                if (other != null) {
                    // 같은 위치에 있는 경우
                    if (step.x == other.x && step.y == other.y) {
                        logger.info(String.format("Collision detected at (%d, %d)", step.x, step.y));
                        return true;
                    }
                    
                    // 교차하는 경우 (에이전트가 서로 자리를 바꾸려는 경우)
                    if (nextSteps.size() > 1 && otherAgentPaths.size() > 1) {
                        Node currentPos = nextSteps.get(0);
                        Node nextPos = nextSteps.get(1);
                        Node otherCurrentPos = otherAgentPaths.get(0);
                        Node otherNextPos = otherAgentPaths.get(1);
                        
                        if (nextPos.x == otherCurrentPos.x && nextPos.y == otherCurrentPos.y &&
                            currentPos.x == otherNextPos.x && currentPos.y == otherNextPos.y) {
                            logger.info(String.format("Path crossing detected between (%d,%d)->(%d,%d) and (%d,%d)->(%d,%d)",
                                currentPos.x, currentPos.y, nextPos.x, nextPos.y,
                                otherCurrentPos.x, otherCurrentPos.y, otherNextPos.x, otherNextPos.y));
                            return true;
                        }
                    }
                    
                    // 인접한 위치도 충돌로 간주
                    if (Math.abs(step.x - other.x) <= 1 && Math.abs(step.y - other.y) <= 1) {
                        logger.info(String.format("Adjacent collision detected between (%d,%d) and (%d,%d)",
                            step.x, step.y, other.x, other.y));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addVisitedPath(String agentName, List<Node> path) {
        Set<String> agentPaths = visitedPaths.computeIfAbsent(agentName, k -> new HashSet<>());
        if (path.size() >= 2) {
            String pathKey = path.get(0).x + "," + path.get(0).y + "->" + 
                           path.get(1).x + "," + path.get(1).y;
            agentPaths.add(pathKey);
            
            // 경로 기억 제한
            if (agentPaths.size() > PATH_MEMORY_LIMIT) {
                agentPaths.clear();  // 너무 많은 경로가 쌓이면 초기화
            }
        }
    }

    private boolean isPathVisited(String agentName, Node current, Node next) {
        Set<String> agentPaths = visitedPaths.getOrDefault(agentName, new HashSet<>());
        String pathKey = current.x + "," + current.y + "->" + next.x + "," + next.y;
        return agentPaths.contains(pathKey);
    }

    private List<Node> findDetourPath(Node current, Node destination, 
                                    List<Node> otherAgentPaths, String agentName) {
        // 우선순위 기반 방향 탐색
        PriorityQueue<DirectionOption> directions = new PriorityQueue<>();
        directions.add(new DirectionOption(0, 1, calculateDirectionScore(current, destination, 0, 1)));
        directions.add(new DirectionOption(1, 0, calculateDirectionScore(current, destination, 1, 0)));
        directions.add(new DirectionOption(0, -1, calculateDirectionScore(current, destination, 0, -1)));
        directions.add(new DirectionOption(-1, 0, calculateDirectionScore(current, destination, -1, 0)));
        
        while (!directions.isEmpty()) {
            DirectionOption dir = directions.poll();
            int newX = current.x + dir.dx;
            int newY = current.y + dir.dy;
            
            if (!isValidPosition(newX, newY)) continue;
            
            Node detourNode = new Node(newX, newY, current);
            if (isPathVisited(agentName, current, detourNode)) continue;
            
            if (!detectCollision(Arrays.asList(detourNode), otherAgentPaths)) {
                List<Node> pathToDestination = findOptimizedPath(
                    newX, newY, destination, otherAgentPaths
                );
                if (!pathToDestination.isEmpty()) {
                    List<Node> completePath = new ArrayList<>();
                    completePath.add(current);
                    completePath.addAll(pathToDestination);
                    addVisitedPath(agentName, Arrays.asList(current, detourNode));
                    return completePath;
                }
            }
        }
        return new ArrayList<>();
    }

    private double calculateDirectionScore(Node current, Node destination, int dx, int dy) {
        int newX = current.x + dx;
        int newY = current.y + dy;
        
        // 목적지까지의 직선 거리
        double distanceToGoal = Math.sqrt(
            Math.pow(destination.x - newX, 2) + 
            Math.pow(destination.y - newY, 2)
        );
        
        // 방향 전환 페널티
        double turnPenalty = (current.parent != null) ? 
            calculateTurnPenalty(current.parent, current, dx, dy) : 0;
        
        return distanceToGoal + turnPenalty;
    }

    private void resetAgentState(String agentName) {
        waitCount.remove(agentName);
        retryCount.remove(agentName);
        stuckCount.remove(agentName);
        stuckWaitStartTime.remove(agentName);
        visitedPaths.remove(agentName);
        cycleDetection.remove(agentName);  // 순환 경로 기록도 초기화
        previousPositions.remove(agentName);  // 이전 위치 기록도 초기화
        waitAttempts.remove(agentName);
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && !grid[x][y];
    }

    private String formatPath(List<Node> path) {
        if (path == null || path.isEmpty()) return "[]";
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < path.size(); i++) {
            Node node = path.get(i);
            sb.append(String.format("(%d,%d)", node.x, node.y));
            if (i < path.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private static class DirectionOption implements Comparable<DirectionOption> {
        int dx, dy;
        double score;

        DirectionOption(int dx, int dy, double score) {
            this.dx = dx;
            this.dy = dy;
            this.score = score;
        }

        @Override
        public int compareTo(DirectionOption other) {
            return Double.compare(this.score, other.score);
        }
    }

    private double calculateTurnPenalty(Node previous, Node current, int dx, int dy) {
        // 이전 방향 계산
        int prevDx = current.x - previous.x;
        int prevDy = current.y - previous.y;
        
        // 방향이 같으면 페널티 없음
        if (prevDx == dx && prevDy == dy) {
            return 0.0;
        }
        
        // 180도 회전은 가장 큰 페널티
        if (prevDx == -dx && prevDy == -dy) {
            return 2.0;
        }
        
        // 90도 회전은 중간 페널티
        return 1.0;
    }

    private List<Node> findOptimizedPath(int startX, int startY, Node destination, List<Node> otherAgentPaths) {
        List<Node> path = globalPathFinder.findGlobalPath(
            startX, startY, destination.x, destination.y, grid
        );
        
        // 경로가 없거나 충돌이 없으면 그대로 반환
        if (path.isEmpty() || !isPathConflicting(path, otherAgentPaths)) {
            return path;
        }
        
        return new ArrayList<>();
    }

    private boolean isPathConflicting(List<Node> path, List<Node> otherAgentPaths) {
        for (int i = 0; i < path.size(); i++) {
            List<Node> nextSteps = new ArrayList<>();
            nextSteps.add(path.get(i));
            
            // 현재 위치에서 충돌 검사
            if (detectCollision(nextSteps, otherAgentPaths)) {
                return true;
            }
        }
        return false;
    }

    private List<Node> findExtendedDetourPath(Node current, Node destination, 
            List<Node> otherAgentPaths, String agentName, int detourDistance) {
        // 현재 위치에서 목적지까지의 직선 거리 계산
        double directDistance = Math.sqrt(
            Math.pow(destination.x - current.x, 2) + 
            Math.pow(destination.y - current.y, 2)
        );

        // 상하좌우 방향으로만 우회 경로 탐색
        int[][] directions = {
            {0, 1},   // 상
            {0, -1},  // 하
            {1, 0},   // 우
            {-1, 0}   // 좌
        };

        List<PathOption> pathOptions = new ArrayList<>();

        // 각 방향으로 우회 거리만큼 이동하면서 경로 탐색
        for (int[] mainDir : directions) {
            // 주 방향으로 이동
            for (int dist = 1; dist <= detourDistance; dist++) {
                int newX = current.x + (mainDir[0] * dist);
                int newY = current.y + (mainDir[1] * dist);

                if (!isValidPosition(newX, newY)) continue;
                if (isPositionInHistory(agentName, newX, newY)) continue;

                // 주 방향으로 이동한 지점에서 목적지 방향으로의 경로 탐색
                Node detourPoint = new Node(newX, newY, null);
                List<Node> pathToDetour = findSafePath(current, detourPoint, otherAgentPaths);
                
                if (!pathToDetour.isEmpty()) {
                    List<Node> pathToDestination = findSafePath(detourPoint, destination, otherAgentPaths);
                    if (!pathToDestination.isEmpty()) {
                        List<Node> completePath = new ArrayList<>();
                        completePath.add(current);
                        completePath.addAll(pathToDetour.subList(1, pathToDetour.size()));
                        completePath.addAll(pathToDestination.subList(1, pathToDestination.size()));
                        
                        // 경로의 품질 평가
                        double score = evaluatePathQuality(completePath, destination);
                        pathOptions.add(new PathOption(completePath, score));
                    }
                }

                // 주 방향에서 측면으로의 우회 시도
                int[][] sideDirections = mainDir[0] == 0 ? 
                    new int[][]{{1, 0}, {-1, 0}} :  // 상하 이동 시 좌우로
                    new int[][]{{0, 1}, {0, -1}};   // 좌우 이동 시 상하로

                for (int[] sideDir : sideDirections) {
                    for (int sideDist = 1; sideDist <= detourDistance; sideDist++) {
                        int sideX = newX + (sideDir[0] * sideDist);
                        int sideY = newY + (sideDir[1] * sideDist);

                        if (!isValidPosition(sideX, sideY)) continue;
                        if (isPositionInHistory(agentName, sideX, sideY)) continue;

                        Node sidePoint = new Node(sideX, sideY, null);
                        List<Node> pathToSide = findSafePath(current, sidePoint, otherAgentPaths);

                        if (!pathToSide.isEmpty()) {
                            List<Node> pathFromSide = findSafePath(sidePoint, destination, otherAgentPaths);
                            if (!pathFromSide.isEmpty()) {
                                List<Node> completePath = new ArrayList<>();
                                completePath.add(current);
                                completePath.addAll(pathToSide.subList(1, pathToSide.size()));
                                completePath.addAll(pathFromSide.subList(1, pathFromSide.size()));

                                double score = evaluatePathQuality(completePath, destination);
                                pathOptions.add(new PathOption(completePath, score));
                            }
                        }
                    }
                }
            }
        }

        // 최적의 경로 선택
        if (!pathOptions.isEmpty()) {
            return Collections.min(pathOptions).path;
        }

        return new ArrayList<>();
    }

    private double evaluatePathQuality(List<Node> path, Node destination) {
        if (path.isEmpty()) return Double.MAX_VALUE;

        double totalLength = path.size();
        int directionChanges = countDirectionChanges(path);
        
        // 목적지까지의 직선 거리
        Node lastNode = path.get(path.size() - 1);
        double directDistance = Math.sqrt(
            Math.pow(destination.x - lastNode.x, 2) + 
            Math.pow(destination.y - lastNode.y, 2)
        );
        
        // 방향 전환 페널티 가중치 증가
        double turnPenalty = directionChanges * 3.0;
        
        // 경로 길이와 목적지까지의 거리에 대한 가중치 조정
        return totalLength * 1.5 + directDistance + turnPenalty;
    }

    private int countDirectionChanges(List<Node> path) {
        if (path.size() < 3) return 0;
        
        int changes = 0;
        for (int i = 2; i < path.size(); i++) {
            int prevDx = path.get(i-1).x - path.get(i-2).x;
            int prevDy = path.get(i-1).y - path.get(i-2).y;
            int currDx = path.get(i).x - path.get(i-1).x;
            int currDy = path.get(i).y - path.get(i-1).y;
            
            if (prevDx != currDx || prevDy != currDy) {
                changes++;
            }
        }
        return changes;
    }

    private List<Node> findNewPath(Node current, Node destination, List<Node> otherAgentPaths, String agentName) {
        // 1. 우선 다른 방향으로의 우회 경로 시도
        for (int detourDistance = 1; detourDistance <= MAX_DETOUR_DISTANCE; detourDistance++) {
            List<Node> detourPath = findExtendedDetourPath(
                current, destination, otherAgentPaths, agentName, detourDistance
            );
            if (!detourPath.isEmpty()) {
                return detourPath;
            }
        }

        // 2. 완전히 다른 경로로 재계획
        List<Node> alternativePath = findAlternativePath(current, destination, otherAgentPaths);
        if (!alternativePath.isEmpty()) {
            return alternativePath;
        }

        // 3. 마지막으로 강제 경로 시도
        return findForcedPath(current, destination, otherAgentPaths);
    }

    private List<Node> findAlternativePath(Node current, Node destination, List<Node> otherAgentPaths) {
        // 현재 위치에서 목적지까지의 직선 거리 계산
        double directDistance = Math.sqrt(
            Math.pow(destination.x - current.x, 2) + 
            Math.pow(destination.y - current.y, 2)
        );

        // 더 넓은 범위에서 우회 지점 탐색
        int searchRadius = (int) (directDistance * 0.5);
        List<Node> potentialPoints = new ArrayList<>();

        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dy = -searchRadius; dy <= searchRadius; dy++) {
                int newX = current.x + dx;
                int newY = current.y + dy;

                if (!isValidPosition(newX, newY)) continue;

                // 현재 위치와 목적지 사이의 중간 지점 선호
                double distanceToCurrent = Math.sqrt(dx * dx + dy * dy);
                if (distanceToCurrent > directDistance * 0.8) continue;

                Node midPoint = new Node(newX, newY, null);
                if (!detectCollision(Arrays.asList(midPoint), otherAgentPaths)) {
                    potentialPoints.add(midPoint);
                }
            }
        }

        // 가능한 중간 지점들을 통한 경로 탐색
        for (Node midPoint : potentialPoints) {
            List<Node> pathToMid = findSafePath(current, midPoint, otherAgentPaths);
            if (!pathToMid.isEmpty()) {
                List<Node> pathToDestination = findSafePath(midPoint, destination, otherAgentPaths);
                if (!pathToDestination.isEmpty()) {
                    List<Node> completePath = new ArrayList<>();
                    completePath.add(current);
                    completePath.addAll(pathToMid.subList(1, pathToMid.size()));
                    completePath.addAll(pathToDestination.subList(1, pathToDestination.size()));
                    return completePath;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Node> findForcedPath(Node current, Node destination, List<Node> otherAgentPaths) {
        // 강제 경로 - 현재 위치에서 목적지까지 직선 경로로 시도
        List<Node> directPath = findStraightPath(current, destination);
        
        // 직선 경로가 실패하면 상하좌우 중 가장 가까운 경로 선택
        if (directPath.isEmpty() || detectCollision(directPath, otherAgentPaths)) {
            int[][] directions = {
                {0, 1}, {1, 0}, {0, -1}, {-1, 0}  // 상, 우, 하, 좌
            };
            
            double minDistance = Double.MAX_VALUE;
            List<Node> bestPath = new ArrayList<>();
            
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                
                if (!isValidPosition(newX, newY)) continue;
                
                Node nextNode = new Node(newX, newY, null);
                double distance = Math.sqrt(
                    Math.pow(destination.x - newX, 2) + 
                    Math.pow(destination.y - newY, 2)
                );
                
                if (distance < minDistance) {
                    minDistance = distance;
                    bestPath = Arrays.asList(current, nextNode);
                }
            }
            
            return bestPath;
        }
        
        return directPath;
    }

    private static class PathOption implements Comparable<PathOption> {
        List<Node> path;
        double score;

        PathOption(List<Node> path, double score) {
            this.path = path;
            this.score = score;
        }

        @Override
        public int compareTo(PathOption other) {
            return Double.compare(this.score, other.score);
        }
    }

    private boolean isPositionInHistory(String agentName, int x, int y) {
        List<Node> positions = previousPositions.getOrDefault(agentName, new ArrayList<>());
        return positions.stream().anyMatch(pos -> pos.x == x && pos.y == y);
    }

    private List<Node> findSafePath(Node start, Node end, List<Node> otherAgentPaths) {
        // 기본 경로 찾기
        List<Node> path = globalPathFinder.findGlobalPath(
            start.x, start.y, end.x, end.y, grid
        );
        
        if (path.isEmpty()) {
            return new ArrayList<>();
        }

        // 목적지 근처의 대기 위치 찾기
        if (isNearDestination(start, end)) {
            return findSafeWaitingPosition(start, end, otherAgentPaths);
        }

        // 경로의 각 지점에서 충돌 검사
        for (Node node : path) {
            if (detectCollision(Arrays.asList(node), otherAgentPaths)) {
                return new ArrayList<>();  // 충돌이 있으면 빈 경로 반환
            }
        }

        return path;
    }

    private boolean isNearDestination(Node current, Node destination) {
        return Math.abs(current.x - destination.x) <= 2 && 
               Math.abs(current.y - destination.y) <= 2;
    }

    private List<Node> findSafeWaitingPosition(Node current, Node destination, List<Node> otherAgentPaths) {
        // 현재 위치가 안전하면 그대로 유지
        if (!detectCollision(Arrays.asList(current), otherAgentPaths)) {
            return Arrays.asList(current);
        }

        // 목적지 주변의 안전한 대기 위치 탐색 (상하좌우 순서로)
        int[][] directions = {
            {0, -1},  // 상
            {0, 1},   // 하
            {-1, 0},  // 좌
            {1, 0}    // 우
        };

        // 목적지로부터 1~3칸 거리에서 안전한 위치 탐색
        for (int distance = 1; distance <= 3; distance++) {
            for (int[] dir : directions) {
                int x = destination.x + (dir[0] * distance);
                int y = destination.y + (dir[1] * distance);

                if (!isValidPosition(x, y)) continue;

                Node waitPos = new Node(x, y, null);
                if (!detectCollision(Arrays.asList(waitPos), otherAgentPaths)) {
                    // 대기 위치까지의 안전한 경로 확인
                    List<Node> pathToWait = globalPathFinder.findGlobalPath(
                        current.x, current.y, x, y, grid
                    );
                    
                    if (!pathToWait.isEmpty() && 
                        !detectCollision(pathToWait, otherAgentPaths)) {
                        return pathToWait;
                    }
                }
            }
        }

        return new ArrayList<>();  // 안전한 대기 위치를 찾지 못한 경우
    }

    private List<int[]> getPrioritizedDirections(Node current, Node destination) {
        List<int[]> directions = new ArrayList<>();
        int dx = destination.x - current.x;
        int dy = destination.y - current.y;

        // 목적지 방향 우선
        if (Math.abs(dx) > Math.abs(dy)) {
            directions.add(new int[]{Integer.signum(dx), 0});  // 주 방향
            directions.add(new int[]{0, Integer.signum(dy)});  // 보조 방향
            directions.add(new int[]{0, -Integer.signum(dy)}); // 반대 보조 방향
            directions.add(new int[]{-Integer.signum(dx), 0}); // 반대 주 방향
        } else {
            directions.add(new int[]{0, Integer.signum(dy)});  // 주 방향
            directions.add(new int[]{Integer.signum(dx), 0});  // 보조 방향
            directions.add(new int[]{-Integer.signum(dx), 0}); // 반대 보조 방향
            directions.add(new int[]{0, -Integer.signum(dy)}); // 반대 주 방향
        }
        return directions;
    }

    private List<Node> tryDetourInDirection(Node current, Node destination, 
            List<Node> otherAgentPaths, int[] dir, int distance) {
        int newX = current.x + (dir[0] * distance);
        int newY = current.y + (dir[1] * distance);

        if (!isValidPosition(newX, newY)) return new ArrayList<>();

        Node detourPoint = new Node(newX, newY, null);
        List<Node> pathToDetour = findStraightPath(current, detourPoint);
        
        if (!pathToDetour.isEmpty() && !detectCollision(pathToDetour, otherAgentPaths)) {
            List<Node> pathToDestination = findStraightPath(detourPoint, destination);
            if (!pathToDestination.isEmpty()) {
                List<Node> completePath = new ArrayList<>();
                completePath.add(current);
                completePath.addAll(pathToDetour.subList(1, pathToDetour.size()));
                completePath.addAll(pathToDestination.subList(1, pathToDestination.size()));
                return completePath;
            }
        }
        return new ArrayList<>();
    }

    private List<Node> findStraightPath(Node start, Node end) {
        List<Node> path = new ArrayList<>();
        path.add(start);
        Node current = start;

        // X축 이동
        while (current.x != end.x) {
            int nextX = current.x + (end.x > current.x ? 1 : -1);
            current = new Node(nextX, current.y, current);
            path.add(current);
        }

        // Y축 이동
        while (current.y != end.y) {
            int nextY = current.y + (end.y > current.y ? 1 : -1);
            current = new Node(current.x, nextY, current);
            path.add(current);
        }

        return path;
    }

    private int calculateDirectionScore(int dirX, int dirY, int targetDx, int targetDy) {
        // 목적지 방향과 일치할수록 높은 점수
        int score = 0;
        if (Math.signum(dirX) == Math.signum(targetDx)) score += 2;
        if (Math.signum(dirY) == Math.signum(targetDy)) score += 2;
        return score;
    }
}
