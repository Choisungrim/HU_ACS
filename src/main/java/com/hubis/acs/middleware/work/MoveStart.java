package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.CommonUtils;
import com.hubis.acs.ui.work.CreateTransferControl;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_move_start")
public class MoveStart extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveStart.class);

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
        robot.setStatus_tx(BaseConstants.ROBOT.STATE.RUNNING);
        baseService.saveOrUpdate(eventInfo,robot);
        if(CommonUtils.isNullOrEmpty(transfer.getLoad_end_at()))
            writerService.sendToUiRobotStateChange(eventInfo, result, robot, transfer.getSource_port_id(), ""); // 적재 전
        else
            writerService.sendToUiRobotStateChange(eventInfo, result, robot, transfer.getDestination_port_id(), transfer.getCarrier_id()); //적재 후

        //작업 세부상태 업데이트
        transfer.setSub_status_tx(BaseConstants.TRANSFER.SUB_STATE.RUNNING);
        baseService.saveOrUpdate(eventInfo,transfer);

        return result;
    }
}
