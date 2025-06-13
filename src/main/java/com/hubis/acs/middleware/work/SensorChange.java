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

import java.util.List;

@Component("middleware_sensor_change")
public class SensorChange extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(SensorChange.class);

    @Autowired
    private MqttCache mqttCache;

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId,siteId));
        if (CommonUtils.isNullOrEmpty(robot)) {
            logger.warn("RobotMaster not found for robotId={}", robotId);
            return BaseConstants.RETURNCODE.Fail;
        }

        for (String field : message.keySet()) {
            Object value = message.opt(field);

            mqttCache.addMqttVehicle(robotId, field, value);
            logger.debug("Updated SensorCache robotId={} field={} value={}", robotId, field, value);
        }


        return result;
    }
}
