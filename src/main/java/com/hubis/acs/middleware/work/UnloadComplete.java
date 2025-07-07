package com.hubis.acs.middleware.work;

import com.hubis.acs.common.cache.MqttCache;
import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.common.utils.TimeUtils;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("middleware_unload_complete")
public class UnloadComplete extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(UnloadComplete.class);

    private final String STOPPED = BaseConstants.ROBOT.TYPE.STATE.STOPPED;

    @Autowired
    private MqttCache mqttCache;

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId, siteId));
        TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(robot.getTransfer_id(), siteId));

        if (isStateValidate(robot) || isStateValidate(transfer)) {
            logger.warn("robot or transfer not found");
            return BaseConstants.RETURNCODE.Fail;
        }

        boolean isValid = false;
        String robotType = robot.getRobot_tp().toLowerCase();

        switch (robotType) {
            case BaseConstants.ROBOT.TYPE.LIFT:
                isValid = checkLift(robot);
                break;
            case BaseConstants.ROBOT.TYPE.CONVEYOR:
                isValid = checkConveyor(robot);
                break;
            case BaseConstants.ROBOT.TYPE.TOWING:
                isValid = checkTowing(robot);
                break;
            default:
                logger.error("Unknown robotType={} for robot={}", robotType, robotId);
                break;
        }

        if (!isValid) {
            logger.error("UnLoadComplete 검증 실패: robotType={} robotId={}", robotType, robotId);
            return BaseConstants.RETURNCODE.Fail;
        }

        logger.info("UnLoadComplete 검증 성공: robotType={} robotId={}", robotType, robotId);
        transfer.setUnload_end_at(TimeUtils.getLocalDateCurrentTime());
        transfer.setSub_status_tx(BaseConstants.TRANSFER.SUB_STATE.UNLOAD_COMPLETE);
        baseService.saveOrUpdate(eventInfo,transfer);

        writerService.sendToUiRobotStateChange(eventInfo, result, robot, transfer.getSource_port_id(), "");
        return result;
    }

    private boolean checkLift(RobotMaster robot) {
        String liftStatus = (String) mqttCache.getMqttVehicleByKey(robot.getRobot_id(), BaseConstants.ROBOT.TYPE.KEY.LIFT_STATUS);
        int sensorStatus = robot.getDetection_fl();

        if(isStateValidate(liftStatus)||isStateValidate(sensorStatus)) return false;
        return (STOPPED.equalsIgnoreCase(liftStatus)
                && sensorStatus == 0);
    }

    private boolean checkConveyor(RobotMaster robot) {
        String conveyorStatus = (String) mqttCache.getMqttVehicleByKey(robot.getRobot_id(), BaseConstants.ROBOT.TYPE.KEY.CONVEYOR_STATUS);
        int sensorStatus = robot.getDetection_fl();

        if(isStateValidate(conveyorStatus)||isStateValidate(sensorStatus)) return false;
        return (STOPPED.equalsIgnoreCase(conveyorStatus)
                && sensorStatus == 0);
    }

    private boolean checkTowing(RobotMaster robot) {
        String towingStatus = (String) mqttCache.getMqttVehicleByKey(robot.getRobot_id(), BaseConstants.ROBOT.TYPE.KEY.TOWING_STATUS);
        int sensorStatus = robot.getDetection_fl();

        if(isStateValidate(towingStatus)||isStateValidate(sensorStatus)) return false;
        return (STOPPED.equalsIgnoreCase(towingStatus)
                && sensorStatus == 0);
    }

    private boolean isStateValidate(Object target) {
        return CommonUtils.isNullOrEmpty(target);
    }
}
