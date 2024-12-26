package com.hubis.acs.service.impl;

import com.hubis.acs.repository.Node;
import com.hubis.acs.service.AlgorithmService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    public List<Node> findGlobalPath(int startX, int startY, int goalX, int goalY, boolean[][] grid) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY, null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            // 목표에 도달한 경우
            if (currentNode.x == goalX && currentNode.y == goalY) {
                return constructPath(currentNode);
            }

            closedList.add(currentNode);

            // 인접 노드 탐색
            for (Node neighbor : getNeighbors(currentNode, grid)) {
                if (closedList.contains(neighbor)) continue;

                // g, h, f 계산
                neighbor.g = currentNode.g + 1; // 이동 비용
                neighbor.h = calculateHeuristic(neighbor, goalX, goalY); // 휴리스틱 계산
                neighbor.f = neighbor.g + neighbor.h;

                // Open List에 이미 존재하는 경우
                if (openList.contains(neighbor)) {
                    // 기존의 f 값이 더 크면 업데이트
                    for (Node node : openList) {
                        if (node.x == neighbor.x && node.y == neighbor.y && node.f > neighbor.f) {
                            openList.remove(node);
                            openList.add(neighbor);
                            break;
                        }
                    }
                } else {
                    openList.add(neighbor);
                }
            }
        }
        return Collections.emptyList(); // 경로를 찾지 못한 경우
    }

    private List<Node> constructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private List<Node> getNeighbors(Node node, boolean[][] grid) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // 상하좌우

        for (int[] direction : directions) {
            int newX = node.x + direction[0];
            int newY = node.y + direction[1];

            // 경계 체크
            if (newX >= 0 && newX < grid.length && newY >= 0 && newY < grid[0].length && !grid[newX][newY]) {
                neighbors.add(new Node(newX, newY, node)); // 부모 노드를 설정
            }
        }
        return neighbors;
    }

    private int calculateHeuristic(Node node, int goalX, int goalY) {
        return Math.abs(node.x - goalX) + Math.abs(node.y - goalY);
    }
}
