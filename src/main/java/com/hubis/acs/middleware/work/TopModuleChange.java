package com.hubis.acs.middleware.work;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("middleware_topmodule_change")
public class TopModuleChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(TopModuleChange.class);
    // 예: static map 으로 초기화
    private static final Map<String, List<String>> ROBOT_TYPE_FIELD_MAP = Map.of(
            "lift", List.of("lift_status", "lift_position"),
            "conveyor", List.of("conveyor_status", "conveyor_position"),
            "towing", List.of("towing_status", "towing_position")
    );

    @Autowired
    private MqttCache mqttCache;

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        handleTopModuleState(robotId, siteId, message);

        return result;
    }

    private void handleTopModuleState(String robotId, String siteId, JSONObject reqMsg) {
        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId,siteId ));
        if (CommonUtils.isNullOrEmpty(robot)) {
            logger.warn("RobotMaster not found for robotId={}", robotId);
            return;
        }

        String robotType = robot.getRobot_tp().toLowerCase();

        List<String> expectedFields = ROBOT_TYPE_FIELD_MAP.get(robotType);
        if (CommonUtils.isNullOrEmpty(expectedFields)) {
            logger.warn("No field map defined for robotType={} robotId={}", robotType, robotId);
            return;
        }

        for (String field : expectedFields) {
            if (reqMsg.has(field)) {
                Object value = reqMsg.opt(field);
                mqttCache.addMqttVehicle(robotId, field, value);
                logger.debug("Updated MqttCache robotId={} field={} value={}", robotId, field, value);
            } else {
                logger.warn("Expected field={} missing in topmodule state message for robotId={}", field, robotId);
            }
        }
    }


}
