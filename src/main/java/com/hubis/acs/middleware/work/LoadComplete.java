package com.hubis.acs.middleware.work;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("middleware_load_complete")
public class LoadComplete extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadComplete.class);

    @Autowired
    private MqttCache mqttCache;


    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId, siteId));
        TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(robot.getTransfer_id(), siteId));

        if (CommonUtils.isNullOrEmpty(robot) || CommonUtils.isNullOrEmpty(transfer)) {
            logger.warn("robot or transfer not found");
            return BaseConstants.RETURNCODE.Fail;
        }

        boolean isValid = false;
        String robotType = robot.getRobot_tp().toLowerCase();

        switch (robotType) {
            case "lift":
                isValid = checkLift(robotId);
                break;
            case "conveyor":
                isValid = checkConveyor(robotId);
                break;
            case "towing":
                isValid = checkTowing(robotId);
                break;
            default:
                logger.error("Unknown robotType={} for robot={}", robotType, robotId);
                break;
        }

        if (isValid) {
            logger.info("LoadComplete 검증 성공: robotType={} robotId={}", robotType, robotId);
            return result;
        } else {
            logger.error("LoadComplete 검증 실패: robotType={} robotId={}", robotType, robotId);
            return BaseConstants.RETURNCODE.Fail;
        }
    }

    private boolean checkLift(String robotId) {
        String liftStatus = (String) mqttCache.getMqttVehicleByKey(robotId,"lift_status");
        String sensor1 = (String) mqttCache.getMqttVehicleByKey(robotId,"sensor_1");

        if(isStateValidate(liftStatus)||isStateValidate(sensor1)) return false;
        return "complete".equalsIgnoreCase(liftStatus) && "detected".equalsIgnoreCase(sensor1);
    }

    private boolean checkConveyor(String robotId) {
        String conveyorStatus = (String) mqttCache.getMqttVehicleByKey(robotId, "conveyor_status");
        String sensor1 = (String) mqttCache.getMqttVehicleByKey(robotId, "sensor_1");
        String sensor2 = (String) mqttCache.getMqttVehicleByKey(robotId, "sensor_2");

        if(isStateValidate(conveyorStatus)||isStateValidate(sensor1)||isStateValidate(sensor2)) return false;
        return ("stopped".equalsIgnoreCase(conveyorStatus) || "ready".equalsIgnoreCase(conveyorStatus))
                && "detected".equalsIgnoreCase(sensor1)
                && "detected".equalsIgnoreCase(sensor2);
    }

    private boolean checkTowing(String robotId) {
        String towingStatus = (String) mqttCache.getMqttVehicleByKey(robotId, "towing_status");
        String sensor1 = (String) mqttCache.getMqttVehicleByKey(robotId, "sensor_1");
        String sensor2 = (String) mqttCache.getMqttVehicleByKey(robotId, "sensor_2");

        if(isStateValidate(towingStatus)||isStateValidate(sensor1)||isStateValidate(sensor2)) return false;
        return "complete".equalsIgnoreCase(towingStatus)
                && "detected".equalsIgnoreCase(sensor1)
                && "detected".equalsIgnoreCase(sensor2);
    }

    private boolean isStateValidate(String target) {
        return CommonUtils.isNullOrEmpty(target);
    }
}
