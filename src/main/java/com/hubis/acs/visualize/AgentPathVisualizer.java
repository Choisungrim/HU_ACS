package com.hubis.acs.visualize;

import com.hubis.acs.repository.Node;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AgentPathVisualizer extends JPanel {
    private List<List<Node>> agentPaths;
    private List<Node> destinations;
    private List<Node> currentPositions;
    private final int cellSize = 40;
    private final int gridWidth = 30;
    private final int gridHeight = 20;

    public AgentPathVisualizer(List<List<Node>> agentPaths, List<Node> agentDestinations, List<Node> currentPositions) {
        this.agentPaths = agentPaths;
        this.destinations = agentDestinations;
        this.currentPositions = new ArrayList<>(currentPositions);
        setPreferredSize(new Dimension(gridWidth * cellSize, gridHeight * cellSize));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 그리드 그리기
        drawGrid(g2d);
        
        // 경로 그리기 (선 두께 증가)
        g2d.setStroke(new BasicStroke(2));
        drawPaths(g2d);
        
        // 현재 위치 그리기
        drawCurrentPositions(g2d);
        
        // 목적지 그리기
        drawDestinations(g2d);
    }

    private void drawGrid(Graphics2D g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                g.drawRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawPaths(Graphics2D g) {
        for (int i = 0; i < agentPaths.size(); i++) {
            List<Node> path = agentPaths.get(i);
            if (path != null && !path.isEmpty()) {
                g.setColor(getAgentColor(i));
                
                for (int j = 0; j < path.size() - 1; j++) {
                    Node current = path.get(j);
                    Node next = path.get(j + 1);
                    g.drawLine(
                        current.x * cellSize + cellSize/2,
                        current.y * cellSize + cellSize/2,
                        next.x * cellSize + cellSize/2,
                        next.y * cellSize + cellSize/2
                    );
                }
            }
        }
    }

    private void drawCurrentPositions(Graphics2D g) {
        for (int i = 0; i < currentPositions.size(); i++) {
            Node pos = currentPositions.get(i);
            g.setColor(getAgentColor(i));
            g.fillOval(
                pos.x * cellSize + cellSize/4,
                pos.y * cellSize + cellSize/4,
                cellSize/2,
                cellSize/2
            );
            g.setColor(Color.BLACK);
            g.drawString("A" + (i+1), 
                pos.x * cellSize + cellSize/3,
                pos.y * cellSize + 2*cellSize/3
            );
        }
    }

    private void drawDestinations(Graphics2D g) {
        for (int i = 0; i < destinations.size(); i++) {
            Node dest = destinations.get(i);
            g.setColor(getAgentColor(i));
            g.setStroke(new BasicStroke(2));
            g.drawRect(
                dest.x * cellSize + cellSize/4,
                dest.y * cellSize + cellSize/4,
                cellSize/2,
                cellSize/2
            );
            g.drawString("D" + (i+1),
                dest.x * cellSize + cellSize/3,
                dest.y * cellSize + 2*cellSize/3
            );
        }
    }

    private Color getAgentColor(int agentIndex) {
        Color[] colors = {
            new Color(255, 0, 0, 200),    // Red
            new Color(0, 0, 255, 200),    // Blue
            new Color(0, 255, 0, 200),    // Green
            new Color(255, 165, 0, 200),  // Orange
            new Color(128, 0, 128, 200)   // Purple
        };
        return colors[agentIndex % colors.length];
    }

    // 현재 위치 업데이트 메소드
    public void updateAgentPositions(List<Node> newPositions) {
        this.currentPositions = new ArrayList<>(newPositions);
        repaint();
    }

    public void updateAgentPath(int agentIndex, List<Node> newPath) {
        if (agentIndex >= 0 && agentIndex < agentPaths.size()) {
            // 새로운 경로로 완전히 교체
            agentPaths.set(agentIndex, new ArrayList<>(newPath));
            // UI 즉시 갱신
            repaint();
        }
    }
}
