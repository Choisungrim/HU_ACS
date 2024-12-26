package com.hubis.acs.service;

import com.hubis.acs.repository.Node;

import java.util.List;

public interface AlgorithmService {

    public List<Node> findGlobalPath(int startX, int startY, int goalX, int goalY, boolean[][] grid);
}
