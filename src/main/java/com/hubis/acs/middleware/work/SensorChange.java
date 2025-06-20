package com.hubis.acs.middleware.work;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("middleware_sensor_change")
public class SensorChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(SensorChange.class);

    @Autowired
    private MqttCache mqttCache;

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();
        int allDetected = 1;

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId,siteId));
        if (CommonUtils.isNullOrEmpty(robot)) {
            logger.warn("RobotMaster not found for robotId={}", robotId);
            return BaseConstants.RETURNCODE.Fail;
        }

        for (String field : message.keySet()) {
            Object value = message.opt(field);

            // 특정 센서 필드에 대해서만 체크하고 싶은 경우 조건 추가
            if (field.startsWith("cargo_")) {
                if (!"1".equalsIgnoreCase(String.valueOf(value))) {
                    allDetected = 0;
                }
            }
        }
        System.out.println("allDetected = " + allDetected);
        System.out.println("robotDetection = " + robot.getDetection_fl());
        if (robot.getDetection_fl() != allDetected) {

            robot.setDetection_fl(allDetected);
            boolean update = baseService.update(eventInfo, robot);
            System.out.println("▶ RobotMaster Sensor Change update result = " + update);
        }

        logger.debug("Updated RobotMaster.detectionFl = {}", allDetected);

        return result;
    }
}
