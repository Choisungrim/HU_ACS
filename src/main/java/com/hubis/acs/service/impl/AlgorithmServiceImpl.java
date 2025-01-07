package com.hubis.acs.service.impl;

import com.hubis.acs.repository.Node;
import com.hubis.acs.service.AlgorithmService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    public List<Node> aStarSearch(int startX, int startY, int endX, int endY, boolean[][] grid) {
        // A* 알고리즘 최적화
        PriorityQueue<Node> openSet = new PriorityQueue<>((a, b) -> a.f - b.f);
        Set<String> closedSet = new HashSet<>();
        Map<String, Node> nodeMap = new HashMap<>();

        Node startNode = new Node(startX, startY, null);
        startNode.g = 0;
        startNode.h = calculateHeuristic(startX, startY, endX, endY);
        startNode.f = startNode.g + startNode.h;

        openSet.offer(startNode);
        nodeMap.put(startX + "," + startY, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.x == endX && current.y == endY) {
                return reconstructPath(current);
            }

            String currentKey = current.x + "," + current.y;
            closedSet.add(currentKey);

            // 4방향 이동만 고려 (성능 최적화)
            int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (isValid(newX, newY, grid)) {
                    String neighborKey = newX + "," + newY;
                    if (closedSet.contains(neighborKey)) {
                        continue;
                    }

                    int tentativeG = current.g + 1;
                    Node neighbor = nodeMap.get(neighborKey);

                    if (neighbor == null) {
                        neighbor = new Node(newX, newY, current);
                        neighbor.g = tentativeG;
                        neighbor.h = calculateHeuristic(newX, newY, endX, endY);
                        neighbor.f = neighbor.g + neighbor.h;
                        openSet.offer(neighbor);
                        nodeMap.put(neighborKey, neighbor);
                    } else if (tentativeG < neighbor.g) {
                        neighbor.parent = current;
                        neighbor.g = tentativeG;
                        neighbor.f = neighbor.g + neighbor.h;
                        // PriorityQueue 재정렬을 위해 제거 후 다시 추가
                        openSet.remove(neighbor);
                        openSet.offer(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private int calculateHeuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private boolean isValid(int x, int y, boolean[][] grid) {
        return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length && !grid[x][y];
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node);
            node = node.parent;
        }
        return path;
    }
}
