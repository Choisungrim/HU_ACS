package com.hubis.acs.scheduler;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.cache.SiteCache;
import com.hubis.acs.common.position.handler.ZoneLockManager;
import com.hubis.acs.common.position.model.Point;
import com.hubis.acs.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransferTaskScheduler {
    private static final Logger log = LoggerFactory.getLogger(TransferTaskScheduler.class);

    private final TaskService taskService;
    private final SiteCache siteCache;
    private final ZoneLockManager zoneLockManager;
    private final MqttCache mqttCache;


    public TransferTaskScheduler(TaskService taskService, SiteCache siteCache, ZoneLockManager zoneLockManager, MqttCache mqttCache) {
        this.taskService = taskService;
        this.siteCache = siteCache;
        this.zoneLockManager = zoneLockManager;
        this.mqttCache = mqttCache;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduleTransferAssignments() {
        for (String siteCd : siteCache.getSiteCdSet()) {
            try {
                taskService.assignReadyTransfers(siteCd);

                Map<String, String> siteZoneLocks = zoneLockManager.getLocksBySite(siteCd);
                if (!siteZoneLocks.isEmpty()) {
                    System.out.println("▶▶ Site "+siteCd+" Zone 점유 상태:");
                    siteZoneLocks.forEach((zoneId, robotId) -> {
                        log.info(" - Zone [{}] → Robot [{}]", zoneId, robotId);
                    });
                } else {
                    System.out.println("▶▶ Site "+siteCd+"점유된 Zone 없음");
                }

                Map<String, Map<String, Object>> cache = mqttCache.getMqttCacheInfo();
                for (String robotId : cache.keySet()) {
                    Map<String, Object> fields = cache.get(robotId);
                    System.out.println("Robot ID: " + robotId + ", Fields: " + fields);
                }

            } catch (Exception e) {
                log.error("[{}] 작업 할당 실패", siteCd, e);
            }
        }

    }

    public void test() {
    }

    private Point parsePoint(Point max, Point min) {
        Point center = new Point((max.x + min.x)/2, (max.y + min.y)/2);

        return center;
    }
}
