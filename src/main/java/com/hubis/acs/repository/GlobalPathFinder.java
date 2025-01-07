package com.hubis.acs.repository;

import com.hubis.acs.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

public class GlobalPathFinder {
    private static final Object pathLock = new Object();  // 경로 계산을 위한 동기화 객체
    private final Map<String, List<Node>> pathCache = new ConcurrentHashMap<>();  // 경로 캐시

    AlgorithmService algorithmService;

    public GlobalPathFinder(AlgorithmService algorithmService)
    {
        this.algorithmService = algorithmService;
    }

    public List<Node> findGlobalPath(int startX, int startY, int endX, int endY, boolean[][] grid) {
        // 캐시 키 생성
        String cacheKey = startX + "," + startY + "," + endX + "," + endY;
        
        // 캐시된 경로가 있는지 확인
        List<Node> cachedPath = pathCache.get(cacheKey);
        if (cachedPath != null) {
            return new ArrayList<>(cachedPath);
        }

        synchronized(pathLock) {
            // 동기화 블록 진입 후 다시 한번 캐시 확인
            cachedPath = pathCache.get(cacheKey);
            if (cachedPath != null) {
                return new ArrayList<>(cachedPath);
            }

            List<Node> path = algorithmService.aStarSearch(startX, startY, endX, endY, grid);
            if (!path.isEmpty()) {
                pathCache.put(cacheKey, new ArrayList<>(path));
            }
            return path;
        }
    }


}
