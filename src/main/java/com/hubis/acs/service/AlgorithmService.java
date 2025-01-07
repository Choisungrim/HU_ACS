package com.hubis.acs.service;

import com.hubis.acs.repository.Node;

import java.util.List;

public interface AlgorithmService {

    public List<Node> aStarSearch(int startX, int startY, int endX, int endY, boolean[][] grid);
}
