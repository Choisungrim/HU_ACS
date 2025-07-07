package com.hubis.acs.middleware.work;

import com.hubis.acs.common.constants.BaseConstants;
import com.hubis.acs.common.entity.RobotMaster;
import com.hubis.acs.common.entity.TransferControl;
import com.hubis.acs.common.entity.vo.RobotMasterId;
import com.hubis.acs.common.entity.vo.TransferControlId;
import com.hubis.acs.common.handler.impl.GlobalWorkHandler;
import com.hubis.acs.common.utils.TimeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("middleware_job_complete")
public class JobComplete extends GlobalWorkHandler {
    private static final Logger logger = LoggerFactory.getLogger(JobComplete.class);

    @Override
    public String doWork(JSONObject message) throws Exception {

        String robotId = eventInfo.getUserId();
        String siteId = eventInfo.getSiteId();

        RobotMaster robot = baseService.findById(RobotMaster.class, new RobotMasterId(robotId, siteId));
        TransferControl transfer = baseService.findById(TransferControl.class, new TransferControlId(robot.getTransfer_id(),siteId));

        robot.setStatus_tx(BaseConstants.ROBOT.STATE.IDLE);
        robot.setTransfer_id("");
        writerService.sendToUiRobotStateChange(eventInfo, result, robot, "", "");
        baseService.saveOrUpdate(eventInfo, robot);

        transfer.setTransfer_status_tx(BaseConstants.TRANSFER.STATE.COMPLETED);
        transfer.setSub_status_tx(BaseConstants.TRANSFER.STATE.COMPLETED);
        transfer.setJob_complete_at(TimeUtils.getLocalDateCurrentTime());
        baseService.saveOrUpdate(eventInfo, transfer);
        baseService.delete(eventInfo, transfer);

        logger.info("Job complete succesful");
        return result;
    }
}