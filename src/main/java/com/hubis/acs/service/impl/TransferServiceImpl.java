package com.hubis.acs.service.impl;

import com.hubis.acs.configuration.AgentRunnable;
import com.hubis.acs.repository.GlobalPathFinder;
import com.hubis.acs.repository.LocalPathPlanner;
import com.hubis.acs.repository.Node;
import com.hubis.acs.service.TransferService;
import com.hubis.acs.visualize.AgentPathVisualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);
    private static JFrame visualizerFrame;  // 정적 변수로 프레임 선언
    private static AgentPathVisualizer visualizer;  // 정적 변수로 시각화 컴포넌트 선언
    private static Timer visualizationTimer;  // 정적 변수로 타이머 선언
    private static List<List<Node>> sharedGlobalPaths;  // 공유 전역 경로 추가

    public void pathFinding(int agentCount) {
        // 기존 창이 있면 닫기
        if (visualizerFrame != null) {
            visualizerFrame.dispose();
        }
        if (visualizationTimer != null) {
            visualizationTimer.stop();
        }

        boolean[][] grid = new boolean[30][30];
        GlobalPathFinder globalPathFinder = new GlobalPathFinder();

        List<Thread> agentThreads = new ArrayList<>();
        List<List<Node>> agentGlobalPaths = new ArrayList<>();
        List<List<Node>> agentLocalPaths = new ArrayList<>();
        List<Node> agentDestinations = new ArrayList<>();
        List<Node> currentPositions = new ArrayList<>();

        // sharedGlobalPaths 초기화를 먼저 수행
        sharedGlobalPaths = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            sharedGlobalPaths.add(new ArrayList<>());
        }

        for (int i = 0; i < agentCount; i++) {
            Random rand = new Random();
            int startX, startY, endX, endY;
            boolean validPosition = false;
            
            // 시작 위치가 다른 에이전트와 충돌하지 않을 때까지 재시도
            do {
                startX = rand.nextInt(10);
                startY = rand.nextInt(10);
                validPosition = true;
                
                // 다른 에이전트들과의 거리 체크
                for (Node existingPos : currentPositions) {
                    if (Math.abs(existingPos.x - startX) < 2 && 
                        Math.abs(existingPos.y - startY) < 2) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);

            // 목적지도 다른 에이전트의 목적지와 충돌하지 않도록 설정
            do {
                endX = rand.nextInt(20);
                endY = rand.nextInt(20);
                validPosition = true;
                
                for (Node existingDest : agentDestinations) {
                    if (Math.abs(existingDest.x - endX) < 2 && 
                        Math.abs(existingDest.y - endY) < 2) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);

            // 전체 경로 계산
            List<Node> globalPath = globalPathFinder.findGlobalPath(startX, startY, endX, endY, grid);
            agentDestinations.add(new Node(endX, endY, null)); 
            currentPositions.add(new Node(startX, startY, null));
            agentGlobalPaths.add(globalPath);
            sharedGlobalPaths.set(i, new ArrayList<>(globalPath));

            // 로컬 경로 플래너 생성
            LocalPathPlanner localPathPlanner = new LocalPathPlanner(grid);

            // 현재 에이전트의 경로를 다른 에이전트의 경로와 함께 전달
            AgentRunnable agentRunnable = new AgentRunnable(
                globalPath,
                "agent" + (i + 1),
                currentPositions.get(i),
                agentDestinations.get(i),
                localPathPlanner,
                getOtherAgentPaths(currentPositions, i),  // 현재 위치만 전달하도록 수정
                agentLocalPaths,
                currentPositions,
                i,
                sharedGlobalPaths
            );

            Thread agentThread = new Thread(agentRunnable);
            agentThreads.add(agentThread);
        }

        // UI 초기화를 EDT에서 실행
        SwingUtilities.invokeLater(() -> {
            // 시각화 컴포넌트 생성
            visualizer = new AgentPathVisualizer(
                agentGlobalPaths, 
                agentDestinations,
                currentPositions
            );
            
            // 프레임 생성 및 설정
            visualizerFrame = new JFrame("Agent Path Visualizer");
            visualizerFrame.add(visualizer);
            visualizerFrame.setSize(1000, 1000);
            visualizerFrame.setLocationRelativeTo(null);
            visualizerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // 타이머 설정
            visualizationTimer = new Timer(1000, e -> {
                visualizer.updateAgentPositions(currentPositions);
                // 경로 업데이트 반영
                for (int i = 0; i < agentCount; i++) {
                    visualizer.updateAgentPath(i, sharedGlobalPaths.get(i));
                }
                visualizerFrame.repaint();
            });
            
            // UI 표시 및 타이머 시작
            visualizerFrame.setVisible(true);
            visualizationTimer.start();
        });

        // 스레드 실행 및 대기
        for (Thread agentThread : agentThreads) {
            agentThread.start();
        }

        try {
            for (Thread agentThread : agentThreads) {
                agentThread.join();
            }
            // 모든 에이전트가 도착했을 때 타이머 정지
            if (visualizationTimer != null) {
                Thread.sleep(1000);
                visualizationTimer.stop();
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted: ", e);
            Thread.currentThread().interrupt();
        }
    }

    private List<Node> getOtherAgentPaths(List<Node> positions, int currentIndex) {
        List<Node> otherPaths = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            if (i != currentIndex) {
                otherPaths.add(positions.get(i));
            }
        }
        return otherPaths;
    }
}
