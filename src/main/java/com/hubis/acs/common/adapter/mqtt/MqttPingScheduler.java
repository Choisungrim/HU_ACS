package com.hubis.acs.common.adapter.mqtt;

import com.hubis.acs.common.adapter.mqtt.Publisher;
import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.cache.SiteCache;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.repository.RobotMasterRepository;
import com.hubis.acs.service.RobotService;
import com.hubis.acs.service.WriterService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MqttPingScheduler {

    private final WriterService writerService;
    private final MqttCache mqttCache;
    private final RobotService robotService;

    // 3초마다 실행 (3000ms)
    @Scheduled(fixedRate = 3000)
    public void sendPing() {

        List<RobotMaster> robotList = robotService.findAllRobots();
        for(RobotMaster robot : robotList) {
            String robotId = robot.getRobot_id();
            String siteId = robot.getSite_cd();
            HashMap<String,Object> vehicleInfo = mqttCache.getMqttVehicle(robotId);
            String tid = TimeUtils.getCurrentTimekey();

            int count = (int) vehicleInfo.getOrDefault("connectionCount", 10);
            long rtt = (long) vehicleInfo.getOrDefault("rtt", 0L);
            mqttCache.updatePingState(robotId, tid);
            mqttCache.initializeSite(robotId, siteId);
            if (count == 0 && robot.getUsable_fl() == 1) {
                // DB에서 비활성화 처리
                robotService.robotDisconnectionStatus(robotId, siteId);
            }

            String responseTid = (String) vehicleInfo.getOrDefault("response_tid", "");

            JSONObject message = new JSONObject();

            message.put("rtt", rtt);
            message.put("connectionCount", count);
            message.put("tid", tid);
            message.put("update_time", tid);


            writerService.sendToMiddlewareHeartbeat(message, robotId);
        }


    }
}

