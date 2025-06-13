package com.hubis.acs.scheduler;

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


    public TransferTaskScheduler(TaskService taskService, SiteCache siteCache, ZoneLockManager zoneLockManager) {
        this.taskService = taskService;
        this.siteCache = siteCache;
        this.zoneLockManager = zoneLockManager;
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduleTransferAssignments() {
        for (String siteCd : siteCache.getSiteCdSet()) {
            try {
                taskService.assignReadyTransfers(siteCd);

                Map<String, String> siteZoneLocks = zoneLockManager.getLocksBySite(siteCd);
                if (!siteZoneLocks.isEmpty()) {
                    log.info("▶▶ Site [{}] Zone 점유 상태:", siteCd);
                    siteZoneLocks.forEach((zoneId, robotId) -> {
                        log.info(" - Zone [{}] → Robot [{}]", zoneId, robotId);
                    });
                } else {
                    log.info("▶▶ Site [{}] 점유된 Zone 없음", siteCd);
                }


            } catch (Exception e) {
                log.error("[{}] 작업 할당 실패", siteCd, e);
            }
        }

    }
//    @Scheduled(fixedDelay = 5000)
    public void test() {
        //MapName: 20250108_hubis_prac_testmap
        //MinPos: -3240 -7080
        //MaxPos: 6100 1620
        //Resolution: 1000

//        MapTransform transform = new MapTransform(
//                parsePoint(new Point(-3240,-7080), new Point(6100, 1620)),// localOriginInGlobal
//                0,                        // 회전 없음
//                1000.0                    // grid 1칸 = 1000
//        );
//
//        Point localGrid = new Point(0, 0); // 로컬맵에서 (0,0) (그리드)
//        Point globalMm = transform.toGlobal(localGrid); // 글로벌(mm 단위)

        // System.out.println(globalMm); // → (-1430.0, 2730.0)
    }

    private Point parsePoint(Point max, Point min) {
        Point center = new Point((max.x + min.x)/2, (max.y + min.y)/2);

        return center;
    }
}
