package com.hubis.acs.middleware.work;

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
import org.springframework.stereotype.Component;

@Component("middleware_unload_start")
public class UnloadStart extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(UnloadStart.class);

    @Override
    public String doWork(JSONObject message) throws Exception {
        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findByEntity(RobotMaster.class, new RobotMaster(robotId, siteId));
        TransferControl transfer = baseService.findByEntity(TransferControl.class, new TransferControl(robot.getTransfer_id(), siteId));

        if(CommonUtils.isNullOrEmpty(robot)) {
            logger.warn("robot not found");
            return BaseConstants.RETURNCODE.Fail;
        }

        if(CommonUtils.isNullOrEmpty(robot.getTransfer_id()))
            logger.warn("robot Assigned Transfer not found");

        //상태 업데이트
        robot.setStatus_tx(BaseConstants.ROBOT.STATE.UNLOADING);
        baseService.saveOrUpdate(eventInfo,robot);
        writerService.sendToUiRobotStateChange(eventInfo, result, robot, transfer.getSource_port_id(), transfer.getCarrier_id());

        transfer.setUnload_start_at(TimeUtils.getLocalDateCurrentTime());
        transfer.setSub_status_tx(BaseConstants.TRANSFER.SUB_STATE.UNLOADING);
        baseService.saveOrUpdate(eventInfo,transfer);

        return result;
    }
}
